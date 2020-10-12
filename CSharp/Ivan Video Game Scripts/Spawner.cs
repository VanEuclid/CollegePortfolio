using UnityEngine;
using System.Collections.Generic;

namespace Arena
{
    public class Spawner : MonoBehaviour
    {
        [SerializeField]
        private List<GameObject> spawnList;
        [SerializeField]
        private int spawnAmount = 1;
        [SerializeField]
        private float spawnRate = 10;
        [SerializeField]
        private LayerMask cantSpawnOn;
        [SerializeField]
        private LayerMask cantSpawnPast;

        private float nextSpawnTime = 0;

        public List<GameObject> GetSpawnList()
        {
            return this.spawnList;
        }

        public int GetSpawnAmount()
        {
            return this.spawnAmount;
        }

        public void SetSpawnAmount(int amount)
        {
            this.spawnAmount = amount;
        }

        public float GetSpawnRate()
        {
            return this.spawnRate;
        }

        public void SetSpawnRate(float rate)
        {
            this.spawnRate = rate;
        }

        void Update()
        {
            if(Time.time > nextSpawnTime)
            {
                for (int j = 0; j < spawnAmount; j++)
                {
                    if (spawnList.Count > 0)
                    {
                        int index = Random.Range(0, spawnList.Count);
                        
                        Spawn(spawnList[index]);
                    }
                }
                nextSpawnTime = Time.time + spawnRate;
            }
        }

        void Spawn(GameObject obj) {
            float x =Random.Range(-5, 6);
            float y = Random.Range(-5, 6);
            Vector3 newPos = new Vector3(transform.position.x + x, transform.position.y + y, 0);
            if (CheckOverlap(newPos) && CheckLineOfSight(newPos))
            {
                Instantiate(obj, newPos, new Quaternion(0, 0, 0, 0), transform);
            } else
            {
                Spawn(obj);
            }
        }

        private bool CheckLineOfSight(Vector3 position)
        {
            Vector3 dir = transform.position - position;
            float distance = Vector3.Distance(position, transform.position);
            RaycastHit2D hit = Physics2D.Raycast(transform.position, dir, distance, cantSpawnPast);
            if (hit)
            {
                return false;
            } else
            {
                return true;
            }
        }

        private bool CheckOverlap(Vector3 position)
        {
            Collider2D coll = Physics2D.OverlapCircle(position, 2, cantSpawnOn);
            if (coll)
            {
                return false;
            }
            return true;
        }
    }
}
