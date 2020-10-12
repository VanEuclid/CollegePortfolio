using UnityEngine;
using System.Collections;
using UnityEngine.UI;

namespace Arena
{
    public class WaveUpdater : MonoBehaviour
    {
        private int currentWave = 0;

        [SerializeField]
        private Text waveText;
        private Text waveInstance;

        private static WaveUpdater instance;

        void Awake()
        {
            instance = this;
        }

        public static WaveUpdater GetInstance()
        {
            return instance;
        }

        void Start()
        {
            waveText = Instantiate<Text>(waveText);
            waveText.transform.SetParent(gameObject.transform, false);
        }

        public void UpdateWave(int wave)
        {
            this.currentWave = wave - 12;
            waveText.text = "Wave: " + currentWave;
        }
    }
}
