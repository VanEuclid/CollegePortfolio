using UnityEngine;
using System.Collections.Generic;
using System;

namespace Arena
{
    public class DebugGUI : Messager
    {
        void OnGUI()
        {
            GUI.Box(new Rect(10, Screen.height-120, 100, 120), "Loader Menu");
            if (GUI.Button(new Rect(20, Screen.height - 40, 80, 20), "Wave Up"))
            {
                WaveSpawnerBehavior wave = FindObjectOfType<WaveSpawnerBehavior>();
                if (wave)
                {
                    wave.TriggerNextWave();
                }
            }
            if (GUI.Button(new Rect(20, Screen.height - 70, 80, 20), "Wave Down"))
            {
                WaveSpawnerBehavior wave = FindObjectOfType<WaveSpawnerBehavior>();
                if (wave)
                {
                    wave.TriggerWave(wave.GetCurrentWave() - 1);
                }
            }

            GUI.Box(new Rect(Screen.width - 120, Screen.height - 160, 120, 160), "God Menu");
            if (GUI.Button(new Rect(Screen.width - 110, Screen.height - 70, 100, 20), "Health Restore"))
            {
                app.NotifyChange(ChangeMessage.RESTORE_HEALTH, GameObject.FindGameObjectWithTag("Player"));
            }
        }
    }
}
