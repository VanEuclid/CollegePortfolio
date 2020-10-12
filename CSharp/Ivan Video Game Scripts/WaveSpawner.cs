using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

namespace Arena
{
    /// <summary>
    /// Class Representing a Gameobject that exhibits Wave spawning mechanics. This object must be told when to spawn an object and in what amount.
    /// This class will then spawn the given object in the given amount with this object's spawnDelay in between each individual spawn.
    /// </summary>
    public class WaveSpawner : Messager
    {
        /// <summary>
        /// The list of spawn types available on this spawner.
        /// </summary>
        [SerializeField]
        private List<GameObject> spawnList;

        /// <summary>
        /// The amount of spawns to pool per spawn type.
        /// </summary>
        [SerializeField]
        private float poolAmount = 10;

        /// <summary>
        /// Amount of time to delay spawning on this spawner
        /// </summary>
        [SerializeField]
        private float spawnDelay = 5;

        /// <summary>
        /// The layer to check for overlaps on the spawn point.
        /// </summary>
        [SerializeField]
        private LayerMask cantSpawnOn;

        /// <summary>
        /// The layer to check for line of sight on for the spawn point.
        /// </summary>
        [SerializeField]
        private LayerMask cantSpawnPast;

        /// <summary>
        /// Dictionary of pooled objects where the key is an index and the value is a list of pooled objects.
        /// </summary>
        private Dictionary<int, List<GameObject>> pooledObjects;

        /// <summary>
        /// Objects that are queued up for spawning by this spawner.
        /// </summary>
        private Queue<GameObject> toSpawn;

        /// <summary>
        /// Returns the number of active and queued objects that have been spawned or will be spawned.
        /// </summary>
        /// <returns></returns>
        public int GetActiveCount()
        {
            int count = 0;
            for (int i = 0; i < pooledObjects.Count; i++)
            {
                count += GetActiveCountInList(pooledObjects[i]);
            }
            return count + toSpawn.Count;
        }

        /// <summary>
        /// Returns the number of available spawn types this object has.
        /// </summary>
        /// <returns></returns>
        public int GetTypeCount()
        {
            return this.spawnList.Count;
        }

        /// <summary>
        /// Returns that amount of game objects that are found to be active within a list of objects.
        /// </summary>
        /// <param name="list">The list of objects to check against.</param>
        /// <returns></returns>
        private int GetActiveCountInList(List<GameObject> list)
        {
            int count = 0;
            for(int i = 0; i < list.Count; i++)
            {
                if (list[i].activeInHierarchy)
                {
                    count++;
                }
            }
            return count;
        }

        /// <summary>
        /// Initializes this objects queue and pooled objects lists and handles pooling its objects.
        /// </summary>
        void Start()
        {
            toSpawn = new Queue<GameObject>();
            pooledObjects = new Dictionary<int, List<GameObject>>();
            for (int i = 0; i < spawnList.Count; i++)
            {
                spawnList[i].SetActive(false);
                List<GameObject> subList = new List<GameObject>();
                for (int j = 0; j < poolAmount; j++)
                {
                    GameObject obj = Instantiate(spawnList[i], gameObject.transform.position, new Quaternion(0, 0, 0, 0), transform) as GameObject;
                    obj.name += " " + j;
                    subList.Add(obj);
                }
                pooledObjects.Add(i, subList);
            }
        }

        /// <summary>
        /// Spawns a game object from this objects pooledObjects dictionary at the given index in the given amount.
        /// </summary>
        /// <param name="index">The dictionary key</param>
        /// <param name="amount">The amount to spawn of the object at the given index</param>
        public void Spawn(int index, int amount)
        {
            List<GameObject> list = GetSpawnList(index, amount);
            foreach(GameObject g in list)
            {
                toSpawn.Enqueue(g);
            }
            if (!IsInvoking("TriggerSpawn"))
            {
                InvokeRepeating("TriggerSpawn", 0f, spawnDelay);
            } 
        }

        /// <summary>
        /// Used by an invoke to trigger a spawn, which dequeus an object from this objects toSpawn queue.
        /// </summary>
        private void TriggerSpawn()
        {
            if (toSpawn.Count == 0)
            {
                CancelInvoke("TriggerSpawn");
            } else
            {
                StartCoroutine(SpawnDelay(toSpawn.Dequeue()));
            }
        }

        /// <summary>
        /// Retrives gameobjects at the given index for spawning as long as that object is not active and is not already queued for spawning.
        /// </summary>
        /// <param name="index">The index of the gameobject to spawn</param>
        /// <param name="amount">the amount to spawn</param>
        /// <returns></returns>
        private List<GameObject> GetSpawnList(int index, int amount)
        {
            List<GameObject> objs = new List<GameObject>();
            List<GameObject> list = pooledObjects[index];

            for(int i = 0, count=0; i < list.Count && count<amount; i++)
            {
                GameObject obj = list[i];
                if(!this.toSpawn.Contains(obj) && !obj.activeInHierarchy)
                {
                    objs.Add(obj);
                    count++;
                }
            }
            return objs;
        }

        IEnumerator SpawnDelay(GameObject obj)
        {
            yield return new WaitForSeconds(.8f);
            Spawn(obj);
        }

        /// <summary>
        /// Spawns an object at this objects transform. If any overlaps were detected or this gameobject does not have a line of sight to obj, recursively calls this method to try again.
        /// </summary>
        /// <param name="obj"></param>
        void Spawn(GameObject obj)
        {
            Vector3 newPos = new Vector3(transform.position.x, transform.position.y, 0);
            if (CheckOverlap(newPos) && CheckLineOfSight(newPos))
            {
                obj.transform.position = newPos;
                obj.SetActive(true);
            }
            else
            {
                Debug.Log(gameObject.name + " failed to spawn");
                //Spawn(obj);
            }
        }

        /// <summary>
        /// Disables all active spawns in this objects pooledObjects map.
        /// </summary>
        public void DisableAllSpawns()
        {
            StopAllCoroutines();
            CancelInvoke();
            foreach(int i in pooledObjects.Keys)
            {
                List<GameObject> list = pooledObjects[i];
                foreach(GameObject o in list)
                {
                    if (o.activeInHierarchy)
                    {
                        o.SetActive(false);
                    }
                }
            }
        }

        /// <summary>
        /// Checks if this object has line of sight to the given position. LOS will be blocked by objects on this object's cantSpawnPast layers.
        /// </summary>
        /// <param name="position"></param>
        /// <returns></returns>
        private bool CheckLineOfSight(Vector3 position)
        {
            Vector3 dir = transform.position - position;
            float distance = Vector3.Distance(position, transform.position);
            RaycastHit2D hit = Physics2D.Raycast(transform.position, dir, distance, cantSpawnPast);
            if (hit)
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        /// <summary>
        /// Checks if the given position is overlaping an object on one of this object's cantSpawnOn layers.
        /// </summary>
        /// <param name="position"></param>
        /// <returns></returns>
        private bool CheckOverlap(Vector3 position)
        {
            Collider2D coll = Physics2D.OverlapCircle(position, 1, cantSpawnOn);
            if (coll)
            {
                return false;
            }
            return true;
        }
    }
}
