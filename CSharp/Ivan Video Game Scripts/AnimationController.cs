using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    public class AnimationController : Controller<AnimationMessage>
    {
        public override void OnNotification(AnimationMessage message, GameObject obj, params object[] opData)
        {
            switch (message)
            {
                case AnimationMessage.WALK:
                    HandleWalk(obj, (float)opData[0]);
                    break;
                case AnimationMessage.TRIGGER:
                    HandleTrigger(obj, (string)opData[0]);
                    break;
            }
        }

        void HandleTrigger(GameObject obj, string triggerName)
        {
            AnimAction action = anim => anim.SetTrigger(triggerName);
            ExecuteAction(obj, "is trying to set a trigger", action);
        }

        void HandleWalk(GameObject obj, float movement)
        {
            AnimAction action = anim => anim.SetFloat("motion", movement);
            ExecuteAction(obj, "is trying to play a walk animation", action);
        }

        void ExecuteAction(GameObject obj, string failMessage, AnimAction action)
        {
            Animator anim = obj.GetComponent<Animator>();
            if (anim)
            {
                action(anim);
            }
            else
            {
                Debug.LogError(obj.name + " " + failMessage + " but doesn't have an animator.");
            }
        }

        private delegate void AnimAction(Animator anim);
    }
}