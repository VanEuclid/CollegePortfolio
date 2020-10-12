using UnityEngine;

namespace Arena
{
    public class UIController : Controller<UIMessage>
    { 

        public override void OnNotification(UIMessage message, GameObject obj, params object[] opData)
        {
            switch (message)
            {
                case UIMessage.SCORE:
                    UpdateScore((int) opData[0]);
                    break;
                case UIMessage.PAUSE_MENU:
                    HandlePauseMenu((bool) opData[0]);
                    break;
                case UIMessage.WAVE:
                    HandleWave((int)opData[0]);
                    break;
            }
        }

        void HandleWave(int waveNumber)
        {
            WaveUpdater wave = WaveUpdater.GetInstance();
            if (wave)
            {
                wave.UpdateWave(waveNumber);
            }
        }

        void HandlePauseMenu(bool enabled)
        {
            /*
            PauseMenuScript pause = PauseMenuScript.GetInstance();
            if (pause)
            {
                pause.gameObject.GetComponent<Canvas>().enabled = enabled;
            }
            */
        }

        void UpdateScore(int newScore)
        {
            ScoreUpdater score = ScoreUpdater.GetInstance();
            if (score)
            {
                score.UpdateScore(newScore);
            }
        }
    }
}
