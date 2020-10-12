using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    public class CyclopsBehavior : EnemyBehavior
    {
        public AudioClip cyclopsDeath;
        public bool live = true;
        
        protected override void Move()
        {
            if (live)
            {
                app.NotifyAction(ActionMessage.MOVE, gameObject, direction);
                app.NotifyAnimation(AnimationMessage.WALK, gameObject, direction.magnitude);
            } 
        }

        public override void OnDamage()
        {
        }

        IEnumerator cyclopsDestroy(float delay)
        {
            yield return new WaitForSeconds(delay);
            this.SetBehaviorEnabled(false);
            gameObject.SetActive(false);
        }


        public override void OnDeath()
        {
            AudioSource audio = GetComponent<AudioSource>();

            audio.clip = cyclopsDeath;
            audio.Play();

            live = false;
            app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "die");

            StartCoroutine(cyclopsDestroy(1));
        }

        protected override void Attack()
        {
            app.NotifyAction(ActionMessage.SLASH, gameObject, direction);
        }

        protected override void Behave()
        {
            Collider2D player = Physics2D.OverlapCircle(transform.position, detectionRadius, detectionLayer);
            if (player)
            {
                GameObject g = player.gameObject;
                target = g.transform.position;
                direction = (target - transform.position).normalized;
            }
            else
            {
                target = Vector3.zero;
            }
            if (target != Vector3.zero)
            {
                if (Vector3.Distance(target, transform.position) < 1)
                {
                    Attack();
                }
                else
                {
                    Move();
                }
                LookInDirection(target);
            }
        }
    }
}
