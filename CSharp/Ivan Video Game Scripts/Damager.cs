using UnityEngine;
using System.Collections;

namespace Arena
{
    /// <summary>
    /// Represents a Gameobject that does damage upon being touched every frame.
    /// </summary>
    public abstract class Damager : Messager
    {
        /// <summary>
        /// The amount of damage to inflict on the target object.
        /// </summary>
        [SerializeField]
        protected int damage = 1;

        /// <summary>
        /// The layer on which objects can be damaged.
        /// </summary>
        [SerializeField]
        protected LayerMask damageableLayer;

        /// <summary>
        /// How far away something can be and still be damaged.
        /// </summary>
        [SerializeField]
        protected float collisionRange = 1;

        /// <summary>
        /// 
        /// </summary>
        /// <returns>The amount of damage this object inflicts.</returns>
        public int GetDamage()
        {
            return this.damage;
        }

        /// <summary>
        /// Set the damage this object should inflict on a target.
        /// </summary>
        /// <param name="damage">The new damage value to inflict on targets.</param>
        public void SetDamage(int damage)
        {
            this.damage = damage;
        }

        /// <summary>
        /// Called when an overlap is detected for this object.
        /// </summary>
        protected virtual void OnOverlap(Collider2D coll)
        {
            app.NotifyChange(ChangeMessage.DAMAGE, coll.gameObject, damage);
        }

        protected virtual void Update()
        {
            Collider2D coll = Physics2D.OverlapCircle(transform.position, collisionRange, damageableLayer);
            if (coll)
            {
                OnOverlap(coll);
            }
        }
    }
}
