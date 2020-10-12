using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    /// <summary>
    /// Represents a GameObject that is able to be damaged.
    /// </summary>
    public class Damageable : Messager
    {
        /// <summary>
        /// The max health this damageable object can have.
        /// </summary>
        [SerializeField]
        private int maxHealth = 3;

        /// <summary>
        /// The current health of the object.
        /// </summary>
        [SerializeField]
        private int health = 3;

        /// <summary>
        /// The amount of time the gameobject has before it is able to take damage again.
        /// </summary>
        [SerializeField]
        private float iFrameTime = .5f;

        /// <summary>
        /// Time when the iFrames stop.
        /// </summary>
        private float iFrameStop;

        /// <summary>
        /// Sets this objects max health.
        /// </summary>
        /// <param name="health">New value for this objects max health.</param>
        public void SetMaxHealth(int health)
        {
            this.maxHealth = health;
        }

        /// <summary>
        /// Sets this objects health.
        /// </summary>
        /// <param name="health">The new value for this objects health.</param>
        public void SetHealth(int health)
        {
            this.health = health;
        }

        /// <summary>
        /// Changes the object's health by the specified amount.
        /// If amount is less than 0, the change is considered damage. 
        /// If amount is greater than 0, the change is considered healing.
        /// </summary>
        /// <param name="amount">The positive or negative amount to change this object's health by.</param>
        public void ChangeHealth(int amount)
        {
            int newHealth = health + amount;
            if (amount < 0)
            {
                if (Time.time > iFrameStop)
                {
                    health = newHealth <= 0 ? 0 : newHealth;
                    iFrameStop = Time.time + iFrameTime;
                    if (IsDead())
                    {
                        app.NotifyAction(ActionMessage.DIE, gameObject);
                    }
                }
            }
            else if (amount > 0)
            {
                health = newHealth > maxHealth ? maxHealth : newHealth;
            }
        }

        /// <summary>
        /// Returns this objects max health
        /// </summary>
        /// <returns>This object's max health.</returns>
        public int GetMaxHealth()
        {
            return this.maxHealth;
        }

        /// <summary>
        /// Returns this objects current health.
        /// </summary>
        /// <returns>This objects current health.</returns>
        public int GetHealth()
        {
            return this.health;
        }

        /// <summary>
        /// Whether or not this objects health is less than or equal to zero.
        /// </summary>
        /// <returns>True if health is less than or equal to 0, false otherwise.</returns>
        public bool IsDead()
        {
            return this.health <= 0;
        }
    }
}
