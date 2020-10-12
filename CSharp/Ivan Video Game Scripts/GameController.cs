using UnityEngine;
using System.Collections;
using System;
using UnityEngine.SceneManagement;

namespace Arena
{
    public class GameController : Controller<GameMessage>
    {
        private GameObject player;
        private GameObject playerInstance;

        private String savePath;

        private bool live = false;
        public Canvas gameOver;

        void Awake()
        {
            savePath = UnityEngine.Application.persistentDataPath + "/savegame.dat";
            gameOver.gameObject.SetActive(false);
        }

        public override void OnNotification(GameMessage message, GameObject obj, params object[] opData)
        {
            switch (message)
            {
                case GameMessage.NEW_GAME:
                    HandleNewGame();
                    break;
                case GameMessage.PAUSE_GAME:
                    HandlePauseGame((bool) opData[0]);
                    break;
                case GameMessage.GAME_OVER:
                    HandleGameOver();
                    break;
            }
        }

        void HandleGameOver()
        {
            app.NotifyGame(GameMessage.PAUSE_GAME, gameObject, true);
            gameOver.gameObject.SetActive(true);
            live = true;
        }

        void HandlePauseGame(bool isPaused)
        {
            Time.timeScale = isPaused ? 0 : 1;
        }

        void HandleStartMenu()
        {
            gameOver.gameObject.SetActive(false);
            LoadLevel(0);
        }

        void Update()
        {
            if (live)
            {
                if (Input.GetKeyDown(KeyCode.Return))
                {
                    app.NotifyGame(GameMessage.PAUSE_GAME, gameObject, false);
                    HandleNewGame();
                    live = false;
                }
                if (Input.GetKeyDown(KeyCode.Escape))
                {
                    app.NotifyGame(GameMessage.PAUSE_GAME, gameObject, false);
                    HandleStartMenu();
                    live = false;
                }
            }

        }

        void HandleNewGame()
        {
            gameOver.gameObject.SetActive(false);
            LoadLevel(1);
        }

        void LoadLevel(int level)
        {
            SceneManager.LoadScene(level, LoadSceneMode.Single);
        }
    }
}
