using UnityEngine;
using System.Collections;
using UnityEngine.UI;

namespace Arena
{
    public class ScoreUpdater : MonoBehaviour
    {
        private int currentScore = 0;

        [SerializeField]
        private Text scoreText;
        private Text scoreInstance;

        private static ScoreUpdater instance;

        void Awake()
        {
            instance = this;
        }

        public static ScoreUpdater GetInstance()
        {
            return instance;
        }

        void Start()
        {
            scoreText = Instantiate<Text>(scoreText);
            scoreText.transform.SetParent(gameObject.transform, false);
        }

        public void UpdateScore(int newScore)
        {
            this.currentScore = newScore;
            scoreText.text = "Score: " + currentScore;
        }
    }
}
