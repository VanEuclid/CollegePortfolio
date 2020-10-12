using UnityEngine;
using System.Collections;
using System;

namespace Arena
{
    /// <summary>
    /// Gives the objects this is attached to shooting functionality.
    /// </summary>
    public class Shooter : Messager
    {
        /// <summary>
        /// The shootable object this object should use as ammo.
        /// </summary>
        [SerializeField]
        private Shootable primaryAmmo;

        /// <summary>
        /// The shootable object that this object should use for it's super shot ammo.
        /// </summary>
        [SerializeField]
        private Shootable secondaryAmmo;

        /// <summary>
        /// The current ammo type being used by this object.
        /// </summary>
        private Shootable currentAmmo;

        /// <summary>
        /// The fire rate for this objects shooting.
        /// </summary>
        [SerializeField]
        private float fireRate = .5f;

        /// <summary>
        /// The additional delay to add to the fire-rate in order to calculate secondary fire rate.
        /// </summary>
        [SerializeField]
        private float secondaryDelay = .5f;

        /// <summary>
        /// The range this shooter has.
        /// </summary>
        [SerializeField]
        private float range = 15f;

        /// <summary>
        /// The shooting pattern this object should use (values 0-3)
        /// </summary>
        [SerializeField]
        private int pattern = 0;

        /// <summary>
        /// Time until next fire.
        /// </summary>
        private float nextFire = 0;

        /// <summary>
        /// The initial fire rate.
        /// </summary>
        private float initFireRate;

        /// <summary>
        /// The initial range.
        /// </summary>
        private float initRange;

        /// <summary>
        /// The initial shooting pattern.
        /// </summary>
        private int initPattern;

        void Start()
        {
            initFireRate = fireRate;
            initRange = range;
            initPattern = pattern;
        }

        /// <summary>
        /// Resets the objects fire rate to its original value.
        /// </summary>
        public void ResetFireRate()
        {
            fireRate = initFireRate;
        }

        /// <summary>
        /// Reset the objects fire range to its original value.
        /// </summary>
        public void ResetRange()
        {
            range = initRange;
        }

        /// <summary>
        /// Resets the shoot pattern to its original value.
        /// </summary>
        public void ResetPattern()
        {
            pattern = initPattern;
        }

        /// <summary>
        /// Shoots ammo in the directions specified using this objects shooting pattern.
        /// </summary>
        /// <param name="direction">The direction to shoot in.</param>
        /// <returns>True if this object was able to fire, false otherwise.</returns>
        public void PrimaryShoot(Vector3 direction)
        {
            if (Time.time > nextFire)
            {
                nextFire = Time.time + fireRate;
                currentAmmo = primaryAmmo;
                switch (pattern)
                {
                    case 0:
                        ShootInPattern(0, direction);
                        break;
                    case 1:
                        ShootInPattern(1, direction);
                        break;
                    case 2:
                        ShootInPattern(2, direction);
                        break;
                    case 3:
                        ShootInPattern(3, direction);
                        break;
                }
            }
        }


        /// <summary>
        /// Shoots secondary ammo in the spcified direction using this object's shooting pattern.
        /// </summary>
        /// <param name="direction">The direction to shoot in.</param>
        /// <returns>True if the shot was successful, false otherwise.</returns>
        public bool SecondaryShoot(Vector3 direction)
        {
            if (Time.time > nextFire + secondaryDelay)
            {
                nextFire = Time.time + fireRate;
                currentAmmo = secondaryAmmo;
                ShootInPattern(0, direction);
                return true;
            }
            else
            {
                return false;
            }
        }

        /// <summary>
        /// Shoots bullet(s) in the specified pattern and direction.
        /// </summary>
        /// <param name="pattern">Which shooting pattern to use for firing.</param>
        /// <param name="direction">The direction to shoot in.</param>
        private void ShootInPattern(int pattern, Vector3 direction)
        {
            Vector3[] directions = GetVectorSet(pattern, direction);
            FireInDirections(directions);

        }

        /// <summary>
        /// Depending on set, retrieves one of four vector configurations which is used for shooting directions.
        /// </summary>
        /// <param name="set"></param>
        /// <param name="direction"></param>
        /// <returns></returns>
        private Vector3[] GetVectorSet(int set, Vector3 direction)
        {
            Vector3 dir = FixDirection(direction); ;
            switch (set)
            {
                case 1:
                    return new Vector3[] { dir, dir * -1 };
                case 2:
                    return new Vector3[] { dir, dir * -1, new Vector3(-dir.y, dir.x, 0f), new Vector3(dir.y, -dir.x, 0f) };
                case 3:
                    return new Vector3[] { Vector3.up, Vector3.left, Vector3.right, Vector3.down, new Vector3(1, 1, 0), new Vector3(-1, 1, 0), new Vector3(-1, -1, 0), new Vector3(1, -1, 0) };
                default:
                    return new Vector3[] { dir };
            }
        }

        /// <summary>
        /// Takes an input normalized direction and if it is equal to Vector3.zero, returns Vector3.down
        /// </summary>
        /// <param name="direction">The direction to fix.</param>
        /// <returns>Vector3.down if input direction equaled Vector3.zero, otherwise returns the normalized input direction.</returns>
        private Vector3 FixDirection(Vector3 direction)
        {
            direction = direction == Vector3.zero ? Vector3.down : direction.normalized;
            return direction;
        }

        /// <summary>
        /// For every direction in the array, fires a shootable object in that direction.
        /// </summary>
        /// <param name="directions"> The desired directions to shoot in</param>
        private void FireInDirections(Vector3[] directions)
        {
            for (int i = 0; i < directions.Length; i++)
            {
                Shootable temp = Instantiate(currentAmmo, gameObject.transform.position, new Quaternion(0, 0, 0, 0)) as Shootable;
                temp.transform.position = transform.position;
                temp.SetRange(range);
                temp.SetDirection(directions[i].normalized, true);
            }
        }

        /// <summary>
        /// Sets this objects fire pattern.
        /// </summary>
        /// <param name="pattern">New value for this objects fire pattern.</param>
        public void SetPattern(int pattern)
        {
            this.pattern = pattern;
        }

        /// <summary>
        /// Sets the fire rate to either 0 or the newRate depending on the newRate's value.
        /// </summary>
        /// <param name="newRate">The value to set the fireRate to, can only be positive. If a negative value is passed, sets fireRate to 0.</param>
        public void SetFireRate(float newRate)
        {
            this.fireRate = newRate >= 0 ? newRate : 0;
            this.nextFire = 0;
        }
    }
}
