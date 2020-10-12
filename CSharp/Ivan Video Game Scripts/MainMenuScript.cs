using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

namespace Arena
{
    public class MainMenuScript : Messager
    {
        [SerializeField]
        private Canvas exitMenu;
        [SerializeField]
        private Button newGame;
        [SerializeField]
        private Button exit;
        [SerializeField]
        private Button no;
        [SerializeField]
        private Button yes;

        public Text controlsExplain;

        void Start()
        {
            exitMenu.enabled = false;
            controlsExplain.enabled = false;
        }

        public void ContinuePressed()
        {
            app.NotifyGame(GameMessage.LOAD_GAME, null);
        }

        public void NewGamePressed()
        {
            controlsExplain.enabled = true;
            newGame.gameObject.SetActive(false);
            exit.gameObject.SetActive(false);

            StartCoroutine(WaitForKeyDown(KeyCode.Return));          
        }

        IEnumerator WaitForKeyDown(KeyCode keyCode)
        {
            while (!Input.GetKeyDown(keyCode))
                yield return null;
            app.NotifyGame(GameMessage.NEW_GAME, null, 1);
        }

        public void ExitPressed()
        {
            newGame.enabled = false;
            exit.enabled = false;
            exitMenu.enabled = true;
        }

        public void YesPressed()
        {
            UnityEngine.Application.Quit();
        }

        public void NoPressed()
        {
            newGame.enabled = true;
            exit.enabled = true;
            exitMenu.enabled = false;
        }
    }
}
