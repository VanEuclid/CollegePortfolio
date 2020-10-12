using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Random = UnityEngine.Random;

namespace Arena
{
    /// <summary>
    /// This class controls wave spawners, telling them when, what, and how to spawn gameobjects.
    /// </summary>
    public class WaveSpawnerBehavior : Messager
    {
        /// <summary>
        /// List of wave spawner objects active in the scene to use.
        /// </summary>
        [SerializeField]
        private List<WaveSpawner> waveSpawners;

        /// <summary>
        /// The boss spawner in the scene for boss rounds.
        /// </summary>
        [SerializeField]
        private WaveSpawner bossSpawner;

        /// <summary>
        /// The maximum spawn amount that a spawn control can have.
        /// </summary>
        [SerializeField]
        private int maxSpawnAmount;

        /// <summary>
        /// How often to have a boss round.
        /// </summary>
        [SerializeField]
        private int bossEvery = 20;

        /// <summary>
        /// How often to add a new spawn controller.
        /// </summary>
        [SerializeField]
        private int addControlEvery = 10;

        /// <summary>
        /// The wave to start on.
        /// </summary>
        [SerializeField]
        private int startOnWaveNumber = 1;

        /// <summary>
        /// The current wave number, determined on the start of this object.
        /// </summary>
        private int currentWaveNumber;

        /// <summary>
        /// The next time to check if all enemies are dead.
        /// </summary>
        private float nextCheck;

        /// <summary>
        /// How often to check if all enemies are dead.
        /// </summary>
        private float checkInterval = 5f;

        /// <summary>
        /// The max amount of available enemies for each wave spawner. If one spawner has two and the rest have 3, this will equal 2.
        /// </summary>
        private int maxEnemyTypes;

        public AudioClip cheer;

        void Start()
        {
            maxEnemyTypes = GetLowestSpawnTypeCount();
            currentWaveNumber = startOnWaveNumber - 1;
        }

        /// <summary>
        /// Returns the current wave
        /// </summary>
        /// <returns></returns>
        public int GetCurrentWave()
        {
            return currentWaveNumber;
        }

        /// <summary>
        /// Based on the round number, calculates the amount of SpawnControls that will be needed upon level start and how much to increment each.
        /// </summary>
        /// <param name="roundNumber"></param>
        /// <returns></returns>
        private LinkedList<SpawnControl> CalculateSpawnControllers(int roundNumber)
        {
            LinkedList<SpawnControl> list = new LinkedList<SpawnControl>();
            int amount;
            int increment;
            if (roundNumber < addControlEvery)
            {
                amount = 1;
            }
            else
            {
                amount = (roundNumber / addControlEvery) + 1;
            }
            increment = roundNumber;
            //increment = roundNumber % addControlEvery==0 ? 1:roundNumber%addControlEvery;
            Debug.Log("Amount: " + amount + " increment: " + increment);
            for (int i = 0; i < amount; i++)
            {
                int init = 0;
                if (i > waveSpawners.Count)
                {
                    init = i % maxEnemyTypes;
                }
                SpawnControl control = new SpawnControl(waveSpawners.Count, maxEnemyTypes, maxSpawnAmount, init);
                for(int j = 0; j < increment; j++)
                {
                    control.Increment();
                }
                list.AddLast(control);
            }
            return list;
        }

        /// <summary>
        /// Iterates through each wave spawner to find the lowest available spawn type count.
        /// </summary>
        /// <returns></returns>
        int GetLowestSpawnTypeCount()
        {
            int lowest = int.MaxValue;
            int current;
            foreach (WaveSpawner w in waveSpawners)
            {
                current = w.GetTypeCount();
                if (current < lowest)
                {
                    lowest = current;
                }
            }
            return lowest;
        }

        /// <summary>
        /// Increments currentWaveNumber and triggers a wave.
        /// </summary>
        public void TriggerNextWave()
        {
            AudioSource audio = GetComponent<AudioSource>();
            audio.clip = cheer;

            audio.Play();
            TriggerWave(currentWaveNumber + 1);
        }

        /// <summary>
        /// Triggers a wave, calculates SpawnControls and if the wave is a boss round, then either spawns the wave or the boss.
        /// </summary>
        public void TriggerWave(int waveNumber)
        {
            if (waveNumber <= 0)
            {
                Debug.LogError("Wave number cannot be less than or equal to zero");
                return;
            }

            EnsureSpawnsDisabled();

            currentWaveNumber = waveNumber;
            app.NotifyUI(UIMessage.WAVE, null, waveNumber);

            if (bossEvery > 0 && waveNumber % bossEvery == 0)
            {
                bossSpawner.Spawn(0, 1);
            }
            else
            {
                LinkedList<SpawnControl> controls = CalculateSpawnControllers(waveNumber);

                int[] spawns = new int[controls.Count];
                int[] types = new int[controls.Count];
                int[] amounts = new int[controls.Count];

                int i = 0;
                foreach (SpawnControl s in controls)
                {
                    int[] param = s.GetSpawnParameters();
                    spawns[i] = param[0];
                    types[i] = param[1];
                    amounts[i] = param[2];
                    i++;
                }
                StartCoroutine(TriggerSpawnWithDelay(2, spawns, types, amounts));
            }
        }

        /// <summary>
        /// Iterates through each spawner and calls the spawner's DisableAllSpawns method.
        /// </summary>
        void EnsureSpawnsDisabled()
        {
            StopAllCoroutines();
            nextCheck = Time.time + checkInterval;
            foreach (WaveSpawner s in waveSpawners)
            {
                s.DisableAllSpawns();
            }
        }

        /// <summary>
        /// spawns the given enemy types in the given amounts at a random spawner.
        /// Yes, I know I don't actually use the spawners array. Its a placeholder for possible future development.
        /// </summary>
        /// <param name="spawners"></param>
        /// <param name="types"></param>
        /// <param name="amounts"></param>
        public void SpawnWave(int[] spawners, int[] types, int[] amounts)
        {
            if (spawners.Length != types.Length && types.Length != amounts.Length)
            {
                throw new System.ArgumentException("All arrays must be of equal length");
            }
            int spawner = Random.Range(0, waveSpawners.Count);
            for (int i = 0; i < spawners.Length; i++)
            {
                waveSpawners[spawner].Spawn(types[i], amounts[i]);
                spawner++;
                spawner = spawner >= waveSpawners.Count ? 0 : spawner;
            }
        }

        /// <summary>
        /// Responsible for checking if all enemies dead and triggers the next wave.
        /// </summary>
        void Update()
        {
            if (Time.time > nextCheck)
            {
                if (AllEnemiesDead())
                {
                    TriggerNextWave();
                }
                nextCheck = Time.time + checkInterval;
            }
        }

        /// <summary>
        /// Triggers the SpawnWave method after the given delay.
        /// </summary>
        /// <param name="delay">Time to delay the spawn</param>
        /// <param name="spawners"></param>
        /// <param name="enemies"></param>
        /// <param name="amounts"></param>
        /// <returns></returns>
        IEnumerator TriggerSpawnWithDelay(float delay, int[] spawners, int[] enemies, int[] amounts)
        {
            yield return new WaitForSeconds(delay);
            SpawnWave(spawners, enemies, amounts);
        }

        /// <summary>
        /// Iterates through each wave spawner and retrieves the amount of active enemies. 
        /// Returns true if none are active, false otherwise.
        /// </summary>
        /// <returns></returns>
        bool AllEnemiesDead()
        {
            int count = 0;
            for (int i = 0; i < waveSpawners.Count; i++)
            {
                count += waveSpawners[i].GetActiveCount();
            }
            count += bossSpawner.GetActiveCount();
            return count == 0;
        }

        /// <summary>
        /// Internal utility class used for controlling the spawning.
        /// </summary>
        private class SpawnControl
        {
            int maxSpawners;
            int spawners;
            int maxMonsters;
            int monster;
            int maxAmount;
            int amount;

            public SpawnControl(int maxSpawners, int maxMonsters, int maxAmount, int initMonster)
            {
                this.maxSpawners = maxSpawners;
                this.maxMonsters = maxMonsters;
                this.maxAmount = maxAmount;
                monster = initMonster;
            }

            public void Increment()
            {
                amount++;
                if (amount > maxAmount)
                {
                    amount = 1;
                    monster++;
                }
                monster = monster > maxMonsters - 1 ? 0 : monster;
            }

            public int[] GetSpawnParameters()
            {
                int[] arr = new int[3];
                arr[0] = Random.Range(0, maxSpawners);
                arr[1] = monster;
                arr[2] = amount;
                return arr;
            }
        }
    }
}
