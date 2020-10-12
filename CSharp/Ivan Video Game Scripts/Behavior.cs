using UnityEngine;
using System.Collections;

namespace Arena
{
    public abstract class Behavior : Messager
    {
        /// <summary>
        /// Whether or not this behavior is enabled.
        /// </summary>
        protected bool behaviorEnabled = true;

        private void Update()
        {
            if (behaviorEnabled)
            {
                Behave();
            }
        }

        public void SetBehaviorEnabled(bool enabled)
        {
            behaviorEnabled = enabled;
        }

        public bool GetBehaviorEnabled()
        {
            return behaviorEnabled;
        }

        /// <summary>
        /// What to execute when behavior is enabled.
        /// </summary>
        protected abstract void Behave();
    }
}
