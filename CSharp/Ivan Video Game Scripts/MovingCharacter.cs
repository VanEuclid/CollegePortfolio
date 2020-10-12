using UnityEngine;
using System.Collections;

namespace Arena
{
    public class MovingCharacter : Messager
    {
        
        /// <summary>
        /// The initial speed this object has upon instantiation.
        /// </summary>
        private float initSpeed;

        /// <summary>
        /// The direction the object is currently moving in.
        /// </summary>
        //private Vector3 moveDirection;

        /// <summary>
        /// How quickly the object moves.
        /// </summary>
        [SerializeField]
        private float moveSpeed = 10;

        /// <summary>
        /// The layers that this object cannot move through.
        /// </summary>
        [SerializeField]
        private LayerMask collisionLayer;

        /// <summary>
        /// How far ahead to detect a collider
        /// </summary>
        private float raycastDistance = 1f;

        /// <summary>
        /// The distance to move the object back by when a collision is detected.
        /// </summary>
        //private float knockbackDistance = .05f;

        void Start()
        {
            initSpeed = moveSpeed;
        }

        /// <summary>
        /// Method describing the movement behavior of this object.
        /// </summary>
        public virtual void Move(Vector3 moveDirection)
        {
            RaycastHit2D hit = Physics2D.Raycast(transform.position, moveDirection, raycastDistance, collisionLayer);
            if (!hit)
            {
                transform.Translate(moveDirection * moveSpeed * Time.deltaTime, Space.World);
                app.NotifyAnimation(AnimationMessage.WALK, gameObject, moveDirection.magnitude);
            }
            /*
            else
            {
                Vector2 hitVector = gameObject.transform.position;
                Vector2 direction = hitVector.normalized * -1;
                gameObject.transform.position = direction * knockbackDistance + hitVector;
            }
            */
        }

        /// <summary>
        /// Sets the player movement speed.
        /// </summary>
        /// <param name="speed"></param>
        public void SetMoveSpeed(float speed)
        {
            moveSpeed = speed;
        }

        /// <summary>
        /// Changes this objects move speed by the specified amount;
        /// </summary>
        /// <param name="amount"></param>
        public void ChangeMoveSpeed(float amount)
        {
            moveSpeed += amount;
        }

        /// <summary>
        /// Returns this objects move speed.
        /// </summary>
        /// <returns>This object's move speed.</returns>
        public float GetMoveSpeed()
        {
            return moveSpeed;
        }

        /// <summary>
        /// Sets this objects movement speed back to its initial speed.
        /// </summary>
        public void ResetSpeed()
        {
            this.moveSpeed = initSpeed;
        }
    }
}
