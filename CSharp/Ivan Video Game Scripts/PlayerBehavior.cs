using UnityEngine;
using System.Collections.Generic;
using System;
using System.Collections;

namespace Arena
{
    /// <summary>
    /// Makes the object this is attached to controllable by the player.
    /// </summary>
    public class PlayerBehavior : Behavior, IOnDeathBehavior
    {
        /// <summary>
        /// The direction from the player character that the mouse is positioned at.
        /// </summary>
        private Vector3 shootDirection;

        private bool paused = false; //Game pause

        private bool alive = true; //Can walk or not

        /// <summary>
        /// Sound clips for Ivan
        /// </summary>
        public AudioClip slashSound;
        public AudioClip rockSound;
        public AudioClip deathSound;
        public AudioClip healSound;

        public Canvas test;

        protected override void Behave()
        {
            if(alive)
            {
                Move();
                shootDirection = GetPointingDirection();
                HandleControls(shootDirection);
            }       
        }

        void Move()
        {
            Vector3 moveDirection = new Vector3(Input.GetAxis("Horizontal"), Input.GetAxis("Vertical"), 0).normalized;
            app.NotifyAction(ActionMessage.MOVE, gameObject, moveDirection);
        }

        private Vector3 GetPointingDirection()
        {
            if (!Camera.main.orthographic)
            {
                Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
                float zPlanePos = 0;
                Vector3 posAtZ = ray.origin + ray.direction * (zPlanePos - ray.origin.z) / ray.direction.z;
                Vector2 point = new Vector2(posAtZ.x, posAtZ.y);
                return new Vector3(point.x, point.y, 0) - gameObject.transform.position;
            }
            Vector3 temp = Input.mousePosition;
            return Camera.main.ScreenToWorldPoint(temp) - gameObject.transform.position;
        }

        void HandleControls(Vector3 direction)
        {
            AudioSource audio = GetComponent<AudioSource>(); //Preparing audio

            if (Input.GetButtonDown("Fire1") || Input.GetButton("Fire1"))
            {
                audio.clip = rockSound;
                audio.Play();
                app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "ranged");
                StartCoroutine(TriggerShoot(.4f));
            }
            if (Input.GetButtonDown("Fire2"))
            {
                audio.clip = slashSound;
                audio.Play();
                app.NotifyAction(ActionMessage.SLASH, gameObject, shootDirection);
                app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "melee");
            }

            if (Input.GetButtonDown("Heal"))
            {
                alive = false;
                audio.clip = healSound;
                audio.Play();
                app.NotifyAction(ActionMessage.HEAL, gameObject, "heal");
                app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "damage");
                StartCoroutine(HealDelay(2));
            }

            if (Input.GetButtonDown("Run"))
            {
                Vector3 moveDirection = new Vector3(Input.GetAxis("Horizontal"), Input.GetAxis("Vertical"), 0).normalized;
                app.NotifyAction(ActionMessage.RUN, gameObject, moveDirection);
            }
            if (Input.GetButtonUp("Run"))
            {
                Vector3 moveDirection = new Vector3(Input.GetAxis("Horizontal"), Input.GetAxis("Vertical"), 0).normalized;
                app.NotifyAction(ActionMessage.UNRUN, gameObject, moveDirection);
            }

            if (Input.GetButtonDown("Pause"))
            {
                paused = !paused;
                app.NotifyGame(GameMessage.PAUSE_GAME, gameObject, paused);
            }
        }

        IEnumerator HealDelay(float delay)
        {
            yield return new WaitForSeconds(delay);
            alive = true;
        }

        IEnumerator TriggerShoot(float delay)
        {
            yield return new WaitForSeconds(delay);
            app.NotifyAction(ActionMessage.SHOOT, gameObject, shootDirection);
        }

        public void OnDeath()
        {
			app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "die");
            AudioSource audio = GetComponent<AudioSource>(); //Preparing audio
            audio.clip = deathSound;
            audio.Play();
            alive = false;    
			StartCoroutine(DelayRestart (2f));
        }

		IEnumerator DelayRestart(float delay){
			yield return new WaitForSeconds (delay);
			this.SetBehaviorEnabled(false);
			//Destroy(gameObject);

            app.NotifyGame(GameMessage.GAME_OVER, gameObject, "dead");
        }
    }
}
