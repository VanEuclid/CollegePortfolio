using UnityEngine;
using System.Collections;

namespace Arena
{
    public abstract class Messager : MonoBehaviour
    {
        public ArenaApplication app { get { return ArenaApplication.GetInstance(); } }
    }

    public enum GameMessage
    {
        NEW_GAME, START_GAME, END_GAME, PAUSE_GAME, SAVE_GAME, LOAD_GAME, GAME_OVER, STOP_GAME, LOAD_LEVEL
    }

    public enum ActionMessage
    {
        SHOOT, DIE, SLASH, MOVE, RUN, UNRUN, HEAL
    }

    public enum ChangeMessage
    {
        DAMAGE, RESTORE_HEALTH
    }

    public enum AnimationMessage
    {
        TRIGGER, WALK
    }

    public enum UIMessage
    {
        SCORE, PAUSE_MENU, WAVE
    }
}