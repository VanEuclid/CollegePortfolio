using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    public class ChangeController : Controller<ChangeMessage>
    {
        public override void OnNotification(ChangeMessage message, GameObject obj, params object[] opData)
        {
            switch (message)
            {
                case ChangeMessage.DAMAGE:
                    HandleDamage(obj, (int)opData[0]);
                    break;
                case ChangeMessage.RESTORE_HEALTH:
                    HandleRestoreHealth(obj);
                    break;
            }
        }

        void HandleRestoreHealth(GameObject obj)
        {
            Damageable dam = obj.GetComponent<Damageable>();
            if (dam)
            {
                dam.SetHealth(dam.GetMaxHealth());
            }
            else
            {
                Debug.Log(obj.name + " is trying to restore health but doesn't have a damageable component");
            }
        }

        void HandleDamage(GameObject obj, int amount)
        {
            Damageable dam = obj.GetComponent<Damageable>();

            if (obj.gameObject.name == "Player")
            {
                app.NotifyAnimation(AnimationMessage.TRIGGER, obj, "damage");
            }

            if (dam)
            {
                dam.ChangeHealth(-amount);
            }
            else
            {
                Debug.Log(obj.name + " was scheduled for damage calculations but doesn't have a damageable component.");
            }
        }
    }
}
