using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    public class MinotaurBehavior : EnemyBehavior
    {
        private bool turn = true;
        private int timer = 0;
        public int timetillTurn = 200; //Rate at which Minotaur 
        public bool alive = true;

        public AudioClip minDeath;
        public AudioClip minCharge;

        public override void OnDamage()
        {
        }

        public override void OnDeath()
        {
            AudioSource audio = GetComponent<AudioSource>();

            audio.clip = minDeath;
            audio.Play();

            StartCoroutine(minDestroy(1));
        }

        IEnumerator minDestroy(float delay)
        {
            yield return new WaitForSeconds(delay);
            this.SetBehaviorEnabled(false);
            gameObject.SetActive(false);
        }

        protected override void Attack()
        {
            app.NotifyAction(ActionMessage.SLASH, gameObject, direction);
        }

        protected override void Move()
        {
            if (alive)
                app.NotifyAction(ActionMessage.MOVE, gameObject, direction);
        }

        protected override void Behave()
        {
            Collider2D player = Physics2D.OverlapCircle(transform.position, detectionRadius, detectionLayer);
            if (turn)
            {
                alive = false;
                StartCoroutine(MinWait(2));
                AudioSource audio = GetComponent<AudioSource>();

                audio.clip = minCharge;
                audio.Play();
                GameObject g = player.gameObject;
                target = g.transform.position;
                direction = (target - transform.position).normalized;

                LookInDirection(target);
                turn = false;
            }

            timer++;

            Attack();
            Move();

            if (timer == timetillTurn)
            {
                turn = true;
                timer = 0;
            }       
        }

        IEnumerator MinWait(float delay)
        {
            yield return new WaitForSeconds(delay);
            alive = true;
        }

    }
}

