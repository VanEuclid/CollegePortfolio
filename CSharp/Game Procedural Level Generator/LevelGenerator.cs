using System.Collections.Generic;
using UnityEngine;

namespace OverClocked
{
    public class LevelGenerator : MonoBehaviour
    {
        private int levelLength;

        private GameObject[] N; //1
        private GameObject[] E; //2
        private GameObject[] S; //3
        private GameObject[] W; //4 (START AND END ROOMS)
        private GameObject[] NS; //5
        private GameObject[] EW; //6
        private GameObject[] NW; //7
        private GameObject[] NE; //8
        private GameObject[] SW; //9
        private GameObject[] SE; //10

        private int Nlen;
        private int Elen;
        private int Slen;
        private int Wlen;
        private int NSlen;
        private int EWlen;
        private int NWlen;
        private int NElen;
        private int SWlen;
        private int SElen;

        int startDifficulty; //Set to decide overall games starting difficulty

        // Use this for initialization
        void Start()
        {
            levelLength = startDifficulty;

            N = Resources.LoadAll<GameObject>("N");
            E = Resources.LoadAll<GameObject>("E");
            S = Resources.LoadAll<GameObject>("S");
            W = Resources.LoadAll<GameObject>("W");
            NS = Resources.LoadAll<GameObject>("NS");
            EW = Resources.LoadAll<GameObject>("EW");
            NW = Resources.LoadAll<GameObject>("NW");
            NE = Resources.LoadAll<GameObject>("NE");
            SW = Resources.LoadAll<GameObject>("SW");
            SE = Resources.LoadAll<GameObject>("SE");

            Nlen = N.Length;
            Elen = E.Length;
            Slen = S.Length;
            Wlen = W.Length;
            NSlen = NS.Length;
            EWlen = EW.Length;
            NWlen = NW.Length;
            NElen = NE.Length;
            SWlen = SW.Length;
            SElen = SE.Length;

        }

        List<GameObject> levelCreate()
        {
            List<GameObject> rooms = new List<GameObject>();
            string connectionOpen = "";

            int choice = Random.Range(1, 4);

            switch (choice)
            {
                case 1:
                    GameObject insertN = N[Random.Range(1, N.Length)];
                    rooms.Add(insertN);
                    connectionOpen = "N";
                    break;
                case 2:
                    GameObject insertE = E[Random.Range(1, E.Length)];
                    rooms.Add(insertE);
                    connectionOpen = "E";
                    break;
                case 3:
                    GameObject insertS = S[Random.Range(1, S.Length)];
                    rooms.Add(insertS);
                    connectionOpen = "S";
                    break;
                case 4:
                    GameObject insertW = W[Random.Range(1, W.Length)];
                    rooms.Add(insertW);
                    connectionOpen = "W";
                    break;
                default:
                    break;
            }

            for (int i = 1; i < levelLength; i++)
            {
                int firstChoice = Random.Range(1, 3);

                if (connectionOpen == "N") //N rooms need any room with a S so we grab any room from those tags that isnt an END room i.e. just S
                {
                    switch (firstChoice)
                    {
                        case 1:
                            GameObject insertNS = NS[Random.Range(1, NS.Length)];
                            rooms.Add(insertNS);
                            connectionOpen = "N";
                            break;
                        case 2:
                            GameObject insertSE = SE[Random.Range(1, SE.Length)];
                            rooms.Add(insertSE);
                            connectionOpen = "E";
                            break;
                        case 3:
                            GameObject insertSW = SW[Random.Range(1, SW.Length)];
                            rooms.Add(insertSW);
                            connectionOpen = "W";
                            break;
                    }
                }
                else if (connectionOpen == "E")
                {
                    switch (firstChoice)
                    {
                        case 1:
                            GameObject insertEW = EW[Random.Range(1, EW.Length)];
                            rooms.Add(insertEW);
                            connectionOpen = "E";
                            break;
                        case 2:
                            GameObject insertNW = NW[Random.Range(1, NW.Length)];
                            rooms.Add(insertNW);
                            connectionOpen = "N";
                            break;
                        case 3:
                            GameObject insertSW = SW[Random.Range(1, SW.Length)];
                            rooms.Add(insertSW);
                            connectionOpen = "S";
                            break;
                    }
                }
                else if (connectionOpen == "S")
                {
                    switch (firstChoice)
                    {
                        case 1:
                            GameObject insertNS = NS[Random.Range(1, NS.Length)];
                            rooms.Add(insertNS);
                            connectionOpen = "S";
                            break;
                        case 2:
                            GameObject insertNW = NW[Random.Range(1, NW.Length)];
                            rooms.Add(insertNW);
                            connectionOpen = "W";
                            break;
                        case 3:
                            GameObject insertNE = NE[Random.Range(1, NE.Length)];
                            rooms.Add(insertNE);
                            connectionOpen = "E";
                            break;
                    }
                }
                else
                {
                    switch (firstChoice)
                    {
                        case 1:
                            GameObject insertSE = SE[Random.Range(1, SE.Length)];
                            rooms.Add(insertSE);
                            connectionOpen = "S";
                            break;
                        case 2:
                            GameObject insertEW = EW[Random.Range(1, EW.Length)];
                            rooms.Add(insertEW);
                            connectionOpen = "W";
                            break;
                        case 3:
                            GameObject insertNE = NE[Random.Range(1, NE.Length)];
                            rooms.Add(insertNE);
                            connectionOpen = "N";
                            break;
                    }
                }
            }

            if (connectionOpen == "N")
            {
                GameObject insertS = S[Random.Range(1, S.Length)];
                rooms.Add(insertS);
            }
            else if (connectionOpen == "E")
            {
                GameObject insertW = W[Random.Range(1, W.Length)];
                rooms.Add(insertW);
            }
            else if (connectionOpen == "S")
            {
                GameObject insertN = N[Random.Range(1, N.Length)];
                rooms.Add(insertN);
            }
            else
            {
                GameObject insertE = E[Random.Range(1, E.Length)];
                rooms.Add(insertE);
            }

            levelLength++;
            return rooms;
        }
    }
}


