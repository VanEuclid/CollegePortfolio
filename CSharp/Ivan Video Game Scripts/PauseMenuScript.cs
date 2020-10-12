using UnityEngine;
using System.Collections;
using UnityEngine.SceneManagement;

namespace Arena
{
    public class PauseMenuScript : Messager
    {
        public void MainMenuClicked()
        {
            app.NotifyGame(GameMessage.STOP_GAME, gameObject);
        }

        public void ExitGameClicked()
        {
            UnityEngine.Application.Quit();
        }
    }
}
