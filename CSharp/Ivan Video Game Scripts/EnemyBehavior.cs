using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    /// <summary>
    /// Basic interface for constructing enemy behavior.
    /// </summary>
    public abstract class EnemyBehavior : Behavior, IOnDeathBehavior, IPoolable
    {

        /// <summary>
        /// The target of this enemy.
        /// </summary>
        protected Vector3 target;

        /// <summary>
        /// The resultant vector determining which direction to fire in.
        /// </summary>
        protected Vector3 direction;

        /// <summary>
        /// How far away this enemy can detect the player.
        /// </summary>
        [SerializeField]
        protected float detectionRadius = 15f;

        /// <summary>
        /// The layer(s) that this enemy can detect objects on.
        /// </summary>
        [SerializeField]
        protected LayerMask detectionLayer;

        /// <summary>
        /// How this object attacks.
        /// </summary>
        protected abstract void Attack();

        /// <summary>
        /// Any special behavior this object should have when it dies. Called by the game controller upon object death.
        /// </summary>
        public abstract void OnDeath();

        /// <summary>
        /// Any special behavior this object should have when it is damaged. Called by the game controller upon damage calculation.
        /// </summary>
        public abstract void OnDamage();

        protected abstract void Move();

        protected virtual void LookInDirection(Vector3 location)
        {
            float angleRad = Mathf.Atan2(location.y - transform.position.y, location.x - transform.position.x);
            float angleDeg = (180 / Mathf.PI) * (angleRad - Mathf.PI / 2);
            this.transform.rotation = Quaternion.Euler(0, 0, angleDeg);
        }

        protected override void Behave()
        {
            Move();
            Attack();
            //LookInDirection(target);
        }

        public void OnEnable()
        {
            this.SetBehaviorEnabled(true);
        }

        public void OnDisable()
        {
            app.NotifyChange(ChangeMessage.RESTORE_HEALTH, gameObject);
        }

        protected IEnumerator DisableAfterTime(float time)
        {
            yield return new WaitForSeconds(time);
            gameObject.SetActive(false);
        }
    }
}
