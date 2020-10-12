using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    public class ActionController : Controller<ActionMessage>
    {
        public override void OnNotification(ActionMessage message, GameObject obj, params object[] opData)
        {
            switch (message)
            {
                case ActionMessage.MOVE:
                    HandleMove(obj, (Vector3)opData[0], 0);
                    break;
                case ActionMessage.SHOOT:
                    HandleShoot(obj, (Vector3)opData[0]);
                    break;
                case ActionMessage.SLASH:
                    HandleSlash(obj, (Vector3) opData[0]);
                    break;
                case ActionMessage.DIE:
                    HandleDeath(obj);
                    break;
                case ActionMessage.RUN:
                    HandleMove(obj, (Vector3)opData[0], 1);
                    break;
                case ActionMessage.HEAL:
                    HandleHeal(obj);
                    break;
                case ActionMessage.UNRUN:
                    HandleMove(obj, (Vector3)opData[0], 2);
                    break;
            }
        }

        void HandleHeal(GameObject obj)
        {
            Damageable heal = obj.GetComponent<Damageable>();
            heal.SetHealth(3);
        }

        void HandleSlash(GameObject obj, Vector3 direction)
        {
            Slasher slash = obj.GetComponent<Slasher>();
            if (slash)
            {
                slash.Slash(direction);
            }
            else
            {
                Debug.Log(obj.name + " is attempting to Slash but does not have a Slasher component.");
            }

        }

        void HandleDeath(GameObject obj)
        {
            IOnDeathBehavior death = obj.GetComponent<IOnDeathBehavior>();
            if (death!=null)
            {
                death.OnDeath();
            }
        }

        void HandleShoot(GameObject obj, Vector3 direction)
        {
            Shooter shoot = obj.GetComponent<Shooter>();
            if (shoot)
            {
                shoot.PrimaryShoot(direction);
            } else
            {
                Debug.Log(obj.name + " is attempting to Shoot but does not have a Shooter component.");
            }
        }

        void HandleMove(GameObject obj, Vector3 direction, int run)
        {
            MovingCharacter move = obj.GetComponent<MovingCharacter>();
            if(run == 1)
            {
                move.ChangeMoveSpeed(10);
            }
            else if(run == 2)
            {
                move.ResetSpeed();
            }

            if (move)
            {
                move.Move(direction);
            } else
            {
                Debug.Log(obj.name + " is trying to move but doesn't have a MovingCharacter componenet");
            }
        }
    }
}
