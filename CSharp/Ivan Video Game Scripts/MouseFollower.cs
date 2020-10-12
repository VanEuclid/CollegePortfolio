using UnityEngine;
using System.Collections;

namespace Arena
{
    public class MouseFollower : MonoBehaviour
    {
        void Update()
        {
            SetRotation(GetMousePoint());
        }

        private Vector3 GetMousePoint()
        {
            if (!Camera.main.orthographic)
            {
                Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition);
                float zPlanePos = 0;
                Vector3 posAtZ = ray.origin + ray.direction * (zPlanePos - ray.origin.z) / ray.direction.z;
                Vector2 point = new Vector2(posAtZ.x, posAtZ.y);
                return new Vector3(point.x, point.y, 0);
            }
            Vector3 temp = Input.mousePosition;
            return Camera.main.ScreenToWorldPoint(temp);
        }

        private void SetRotation(Vector3 location)
        {
            float angleRad = Mathf.Atan2(location.y - transform.position.y, location.x - transform.position.x);
            float angleDeg = (180 / Mathf.PI) * (angleRad - Mathf.PI/2);
            this.transform.rotation = Quaternion.Euler(0, 0, angleDeg);
        }
    }
}
