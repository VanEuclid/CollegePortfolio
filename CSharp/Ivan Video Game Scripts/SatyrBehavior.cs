using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    public class SatyrBehavior : EnemyBehavior
    {
        private Vector3 shootDirection;
        public AudioClip satyrAttack;
        public AudioClip satyrDeath;

        protected override void Move()
        {
			if (Vector3.Distance (target, gameObject.transform.position) > 5f) {
				app.NotifyAction(ActionMessage.MOVE, gameObject, direction);
				app.NotifyAnimation(AnimationMessage.WALK, gameObject, direction.magnitude);
			}
        }

        public override void OnDamage()
        {

        }

        IEnumerator satyrDestroy(float delay)
        {
            yield return new WaitForSeconds(delay);
            this.SetBehaviorEnabled(false);
            gameObject.SetActive(false);
        }


        public override void OnDeath()
        {
            AudioSource audio = GetComponent<AudioSource>();

            audio.clip = satyrDeath;
            audio.Play();

            app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "die");

            StartCoroutine(satyrDestroy(1));
        }

        protected override void Attack()
        {
            AudioSource audio = GetComponent<AudioSource>();

            audio.clip = satyrAttack;
            audio.Play();

			app.NotifyAnimation(AnimationMessage.TRIGGER, gameObject, "ranged");
            app.NotifyAction(ActionMessage.SHOOT, gameObject, shootDirection);
        }

        protected override void Behave()
        {
            Collider2D player = Physics2D.OverlapCircle(transform.position, detectionRadius, detectionLayer);
            if (player)
            {
                GameObject g = player.gameObject;
                target = g.transform.position;
                direction = (target - transform.position).normalized;
                shootDirection = direction; 
            }
            else
            {
                target = Vector3.zero;
            }

            if (target != Vector3.zero)
            {
                if (Vector3.Distance(target, transform.position) < 14)
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


