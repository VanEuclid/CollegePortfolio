using UnityEngine;
using System.Collections.Generic;
using UnityEngine.UI;

namespace Arena
{
    public class HealthUpdater : StatUpdater
    {

        private static HealthUpdater instance;

        void Awake()
        {
            instance = this;
        }

        public static HealthUpdater GetInstance()
        {
            return instance;
        }

        protected override void Update()
        {
            if (player)
            {
                Damageable dam = player.GetComponent<Damageable>();
                if (dam)
                {
                    UpdateIcons(dam.GetHealth(), dam.GetMaxHealth());
                }
            } else
            {
                base.Update();
            }
        }
    }
}
