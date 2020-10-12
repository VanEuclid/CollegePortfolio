using UnityEngine;
using System.Collections;

namespace Arena
{
    public abstract class Controller<T> : Messager
    {
        public abstract void OnNotification(T message, GameObject obj, params object[] opData);
    }
}
