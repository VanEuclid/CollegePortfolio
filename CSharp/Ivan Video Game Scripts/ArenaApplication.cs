using UnityEngine;
using System.Collections;

namespace Arena
{
    public class ArenaApplication : MonoBehaviour
    {
        [SerializeField]
        private GameController gameController;
        [SerializeField]
        private ActionController actionController;
        [SerializeField]
        private ChangeController changeController;
        [SerializeField]
        private AnimationController animationController;
        [SerializeField]
        private UIController uiController;


        private static ArenaApplication instance;

        public static ArenaApplication GetInstance()
        {
            return instance;
        }

        void Awake()
        {
            if(instance == null)
            {
                DontDestroyOnLoad(gameObject);
                instance = this;
            } else if (instance != this)
            {
                Destroy(gameObject);
            }
        }

        public void NotifyAction(ActionMessage message, GameObject obj, params object[] opData)
        {
            actionController.OnNotification(message, obj, opData);
        }

        public void NotifyGame(GameMessage message, GameObject obj, params object[] opData)
        {
            gameController.OnNotification(message, obj, opData);
        }

        public void NotifyChange(ChangeMessage message, GameObject obj, params object[] opData)
        {
            changeController.OnNotification(message, obj, opData);
        }

        public void NotifyAnimation(AnimationMessage message, GameObject obj, params object[] opData)
        {
            animationController.OnNotification(message, obj, opData);
        }

        public void NotifyUI(UIMessage message, GameObject obj, params object[] opData)
        {
            uiController.OnNotification(message, obj, opData);
        }
    }
}
