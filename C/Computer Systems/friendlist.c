/*
 * friendlist.c - [Starting code for] a web-based friend-graph manager.
 *
 * Based on:
 *  tiny.c - A simple, iterative HTTP/1.0 Web server that uses the 
 *      GET method to serve static and dynamic content.
 *   Tiny Web server
 *   Dave O'Hallaron
 *   Carnegie Mellon University
 */
#include "csapp.h"
#include "dictionary.h"
#include "more_string.h"

static void doit(int fd);
static dictionary_t *read_requesthdrs(rio_t *rp);
static void read_postquery(rio_t *rp, dictionary_t *headers, dictionary_t *d);
static void clienterror(int fd, char *cause, char *errnum, 
                        char *shortmsg, char *longmsg);
static void print_stringdictionary(dictionary_t *d);
static void serve_request(int fd, dictionary_t *query);

static void serve_greet(int fd, dictionary_t *query);
static void serve_friends(int fd, dictionary_t *query);
static void serve_befriend(int fd, dictionary_t *query);
static void serve_unfriend(int fd, dictionary_t *query);
static void serve_introduce(int fd, dictionary_t *query);
void* serve_client_thread(void* args);

static dictionary_t* serverList;
static char* globalHost;
static char* globalPort;
static pthread_mutex_t mutex;

int main(int argc, char **argv) 
{
  int listenfd, connfd;
  char hostname[MAXLINE], port[MAXLINE];
  socklen_t clientlen;
  struct sockaddr_storage clientaddr;

  /* Check command line args */
  if (argc != 2) {
    fprintf(stderr, "usage: %s <port>\n", argv[0]);
    exit(1);
  }

  pthread_mutex_init(&mutex, NULL); //MUTEX INIT

  listenfd = Open_listenfd(argv[1]);
  globalPort = argv[1]; //SETS GLOBAL PORT FOR THE CONNECTION

  /* Don't kill the server if there's an error, because
     we want to survive errors due to a client. But we
     do want to report errors. */
  exit_on_error(0);

  /* Also, don't stop on broken connections: */
  Signal(SIGPIPE, SIG_IGN);

  serverList = make_dictionary(COMPARE_CASE_SENS,(free_proc_t) free_dictionary); //START GLOBAL FRIEND LIST

  while (1) {
    clientlen = sizeof(clientaddr);
    connfd = Accept(listenfd, (SA *)&clientaddr, &clientlen);
    if (connfd >= 0) {
      Getnameinfo((SA *) &clientaddr, clientlen, hostname, MAXLINE, 
                  port, MAXLINE, 0);
      //printf("Accepted connection from (%s, %s)\n", hostname, port);
      globalHost = hostname; //SETS GLOBAL HOST FOR THE CONNECTION

      pthread_t tid;
      int* targs = malloc(sizeof(int));
      *targs = connfd;
      pthread_create(&tid, NULL, serve_client_thread, targs);
      pthread_detach(tid);

      //doit(connfd);
      //Close(connfd);
    }
  }
}

void* serve_client_thread(void* args)
{
  doit(*(int*)args);
  free(args);

  return NULL;
}

/*
 * doit - handle one HTTP request/response transaction
 */
void doit(int fd) 
{
  char buf[MAXLINE], *method, *uri, *version;
  rio_t rio;
  dictionary_t *headers, *query;

  /* Read request line and headers */
  Rio_readinitb(&rio, fd);
  if (Rio_readlineb(&rio, buf, MAXLINE) <= 0) {
    return;
  }
  printf("%s", buf);
  
  if (!parse_request_line(buf, &method, &uri, &version)) {
    clienterror(fd, method, "400", "Bad Request",
                "Friendlist did not recognize the request");
  } else {
    if (strcasecmp(version, "HTTP/1.0")
        && strcasecmp(version, "HTTP/1.1")) {
      clienterror(fd, version, "501", "Not Implemented",
                  "Friendlist does not implement that version");
    } else if (strcasecmp(method, "GET")
               && strcasecmp(method, "POST")) {
      clienterror(fd, method, "501", "Not Implemented",
                  "Friendlist does not implement that method");
    } else {
      headers = read_requesthdrs(&rio);

      /* Parse all query arguments into a dictionary */
      query = make_dictionary(COMPARE_CASE_SENS, free);
 
      parse_uriquery(uri, query);
      if (!strcasecmp(method, "POST"))
        read_postquery(&rio, headers, query);

      /* For debugging, print the dictionary */
      printf("query dictionary\n");
      print_stringdictionary(query);
      printf("header dictionary\n");
      print_stringdictionary(headers);
      printf("server dictionary\n");
      print_stringdictionary(serverList);

      /* You'll want to handle different queries here,
         but the intial implementation always returns
         nothing: */
      //serve_request(fd, query);

      if(starts_with("/greet", uri))
	serve_greet(fd, query);
      else if(starts_with("/friends", uri))
	serve_friends(fd, query);
      else if(starts_with("/befriend", uri))
	serve_befriend(fd, query);
      else if(starts_with("/unfriend", uri))
	serve_unfriend(fd, query);
      else if(starts_with("/introduce", uri))
	serve_introduce(fd, query);      
      else
	serve_request(fd, query);

      /* Clean up */
      free_dictionary(query);
      free_dictionary(headers);
    }

    /* Clean up status line */
    free(method);
    free(uri);
    free(version);
  }

  Close(fd); //FINALLY close thread fd
}

/*
 * read_requesthdrs - read HTTP request headers
 */
dictionary_t *read_requesthdrs(rio_t *rp) 
{
  char buf[MAXLINE];
  dictionary_t *d = make_dictionary(COMPARE_CASE_INSENS, free);

  Rio_readlineb(rp, buf, MAXLINE);
  printf("%s", buf);
  while(strcmp(buf, "\r\n")) {
    Rio_readlineb(rp, buf, MAXLINE);
    printf("%s", buf);
    parse_header_line(buf, d);
  }
  
  return d;
}

void read_postquery(rio_t *rp, dictionary_t *headers, dictionary_t *dest)
{
  char *len_str, *type, *buffer;
  int len;
  
  len_str = dictionary_get(headers, "Content-Length");
  len = (len_str ? atoi(len_str) : 0);

  type = dictionary_get(headers, "Content-Type");
  
  buffer = malloc(len+1);
  Rio_readnb(rp, buffer, len);
  buffer[len] = 0;

  if (!strcasecmp(type, "application/x-www-form-urlencoded")) {
    parse_query(buffer, dest);
  }

  free(buffer);
}

static char *ok_header(size_t len, const char *content_type) {
  char *len_str, *header;
  
  header = append_strings("HTTP/1.0 200 OK\r\n",
                          "Server: Friendlist Web Server\r\n",
                          "Connection: close\r\n",
                          "Content-length: ", len_str = to_string(len), "\r\n",
                          "Content-type: ", content_type, "\r\n\r\n",
                          NULL);
  free(len_str);

  return header;
}

/*
 * serve_request - example request handler
 */
static void serve_request(int fd, dictionary_t *query)
{
  size_t len;
  char *body, *header;

  body = strdup("error in request format\n");

  len = strlen(body);

  /* Send response headers to client */
  header = ok_header(len, "text/html; charset=utf-8");
  Rio_writen(fd, header, strlen(header));
  printf("Response headers:\n");
  printf("%s", header);

  free(header);

  /* Send response body to client */
  Rio_writen(fd, body, len);

  free(body);
}

static void serve_introduce(int fd, dictionary_t *query)
{
  size_t len;
  char *body, *header;

  len = 0;
  body = NULL;

  char* user = dictionary_get(query, "user");
  printf("user = %s\n", user);

  char* host = dictionary_get(query, "host");
  printf("host = %s\n", host);

  char* port = dictionary_get(query, "port");
  printf("port = %s\n", port);

  char* friend = dictionary_get(query, "friend");
  printf("friend = %s\n\n", friend);

  int friendsAdd = 1;
  char** serverGroup = NULL;
  char* request;
  size_t rLen;

  pthread_mutex_lock(&mutex); //MUTEX LOCK

  printf("CHECK FOR SAME SERVER INTRODUCE == %s, %s, %s, %s\n", host, port, globalHost, globalPort);

  if(strcmp(port, globalPort) || strcmp(host, globalHost)) { //means host port are different

    request = append_strings("GET /friends?user=", friend, " HTTP/1.1\r\n", NULL); //request to send
    rLen = strlen(request);
    
    int client = Open_clientfd(host, port); //CREATE NEW CLIENT 

    Rio_writen(client, request, strlen(request)); //Send request

    header = ok_header(rLen, "text/html; charset=utf-8"); //Send header
    Rio_writen(client, header, strlen(header));
    printf("Response headers:\n");
    printf("%s", header);

    rio_t rio;
    char buf[MAXLINE];
    Rio_readinitb(&rio, client); //init start reading from target

    Rio_readlineb(&rio, buf, MAXLINE);

    printf("============status check ========== %s\n", buf);
    char* version;
    char* status;
    char* desc;
    if(parse_status_line((const char*)buf, &version, &status, &desc)) {
      printf("status line seen\nbuf = %s\nversion = %s\nstatus = %s\ndesc = %s\n\n", buf, version, status, desc);
      char* versionC = "HTTP/1.0";
      char* statusC = "200";
      char* descC = "OK";
      if(strcmp(version, versionC) && strcmp(status, statusC) && strcmp(desc, descC)) {
	printf("bad status received == %s\n", buf);
	close(client);
	free(header);
	free(request);
	free(version);
	free(status);
	free(desc);      
	return;
      }
      else {
	printf("status received == %s", buf);
	free(version);
	free(status);
	free(desc);
      }
    }

    dictionary_t *headers = read_requesthdrs(&rio);
    char* amt = dictionary_get(headers, "Content-length"); //how many bytes to get
    
    printf("amt = %s\n", amt);

    Rio_readnb(&rio, buf,(size_t) amt);
    serverGroup = split_string(buf, '\n');

    close(client);
    free(headers);
    free(request);

  } else { //means host port are the same
    dictionary_t *targetFriends = dictionary_get(serverList, friend);

    if(targetFriends == NULL)
      friendsAdd = 0;
    else
      serverGroup = (char**)dictionary_keys(targetFriends);
  }

  char* current;
  dictionary_t *userFriends;
  dictionary_t *friendsFriends;
  dictionary_t *friends;
  int count = 0;
  if(friendsAdd) { //add friend groups

    while(1) {
      current = serverGroup[count];
  
      if(current == NULL)
	break;

      printf("%d: current = %s\n", count, current);

      userFriends = dictionary_get(serverList, user);
      friendsFriends = dictionary_get(serverList, current);

      if(strcmp(user, current)) {
	if(userFriends == NULL) {
	  friends = make_dictionary(COMPARE_CASE_SENS, NULL);
	  dictionary_set(friends, current, NULL);
	  dictionary_set(serverList, user, friends);
	} else {
	  dictionary_set(userFriends, current, NULL);
	}

	if(friendsFriends == NULL) {
	  friends = make_dictionary(COMPARE_CASE_SENS, NULL);
	  dictionary_set(friends, user, NULL);
	  dictionary_set(serverList, current, friends);
	} else {
	  dictionary_set(friendsFriends, user, NULL);
	}
      }

      count++;
    }
  }

  userFriends = dictionary_get(serverList, user);
  friendsFriends = dictionary_get(serverList, friend);

  if(strcmp(user, friend)) { //add friend
    if(userFriends == NULL) {
      friends = make_dictionary(COMPARE_CASE_SENS, NULL);
      dictionary_set(friends, friend, NULL);
      dictionary_set(serverList, user, friends);
    } else {
      dictionary_set(userFriends, friend, NULL);
    }

    if(friendsFriends == NULL) {
      friends = make_dictionary(COMPARE_CASE_SENS, NULL);
      dictionary_set(friends, user, NULL);
      dictionary_set(serverList, friend, friends);
    } else {
      dictionary_set(friendsFriends, user, NULL);
    }     
  }

  pthread_mutex_unlock(&mutex); //MUTEX UNLOCK

  header = ok_header(len, "text/html; charset=utf-8");
  Rio_writen(fd, header, strlen(header));
  printf("Response headers:\n");
  printf("%s", header);

  free(header);

  Rio_writen(fd, body, len);

  free(serverGroup);
  free(body);
}

/*
 * serve_friends - friends handler
 * displays friends of user
 */
static void serve_friends(int fd, dictionary_t *query) 
{
  size_t len;
  char *body, *header;

  char* user = dictionary_get(query, "user");

  pthread_mutex_lock(&mutex); //MUTEX LOCK

  dictionary_t* userFriends = dictionary_get(serverList, user);

  if(userFriends != NULL) {
    const char** keys = dictionary_keys(userFriends);
    body = join_strings(keys, '\n');
    len = strlen(body);
    free(keys);
  }
  else {
    len = 0;
    body = NULL;
  }

  pthread_mutex_unlock(&mutex); //MUTEX UNLOCK

  //printf("body = %s, len = %zu\n", body, len);

  /* Send response headers to client */
  header = ok_header(len, "text/html; charset=utf-8");
  Rio_writen(fd, header, strlen(header));
  printf("Response headers:\n");
  printf("%s", header);

  free(header);

  /* Send response body to client */
  Rio_writen(fd, body, len);
  
  free(body);
}

static void serve_unfriend(int fd, dictionary_t* query)
{
  size_t len;
  char *body, *header;

  char* user = dictionary_get(query, "user");
  char* unfriendList = dictionary_get(query, "friends");
  char** unfriendGroup = split_string(unfriendList, '\n');

  pthread_mutex_lock(&mutex); //MUTEX LOCK

  char* current;
  dictionary_t* userFriends;
  dictionary_t* friendsFriends;
  int count = 0;
  while(1) {

    current = unfriendGroup[count];

    if(current == NULL)
      break;

    printf("%d: current = %s\n", count, current);

    userFriends = dictionary_get(serverList, user); //Get dictionary of user's friends
    friendsFriends = dictionary_get(serverList, current); //Get dictionary of friend's friends

    if(userFriends != NULL) {
      dictionary_remove(userFriends, current);
    }

    if(friendsFriends != NULL) {
      dictionary_remove(friendsFriends, user);
    }

    count++;
  }

  pthread_mutex_unlock(&mutex); //MUTEX UNLOCK

  dictionary_t* printFriends = dictionary_get(serverList, user); //grab altered value dictionary for user
  if(printFriends != NULL) {
    const char** keys = dictionary_keys(printFriends); //ensure key is freed later
    body = join_strings(keys, '\n');;
    len = strlen(body);
    free(keys);
  }
  else {
    len = 0;
    body = NULL;
  }

  /* Send response headers to client */
  header = ok_header(len, "text/html; charset=utf-8");
  Rio_writen(fd, header, strlen(header));
  printf("Response headers:\n");
  printf("%s", header);

  free(header);

  /* Send response body to client */
  Rio_writen(fd, body, len);

  free(unfriendGroup);
  free(body);
}

/*
 * serve_befriend - befriend handler
 * befriends users
 */
static void serve_befriend(int fd, dictionary_t *query) 
{
  size_t len;
  char *body, *header;

  char* user = dictionary_get(query, "user");
  char* befriendingList = dictionary_get(query, "friends"); //gets value at friends
  char** friendGroup = split_string(befriendingList, '\n'); //splits befriending into array where it splits at \n

  pthread_mutex_lock(&mutex); //MUTEX LOCK

  char* current;
  dictionary_t* userFriends;
  dictionary_t* friendsFriends;
  dictionary_t* friends;
  int count = 0;
  while(1) {

    current = friendGroup[count];

    if(current == NULL)
      break;

    printf("%d: current = %s\n", count, current);

    userFriends = dictionary_get(serverList, user);
    friendsFriends = dictionary_get(serverList, current);

    if(strcmp(user, current)) {
      if(userFriends == NULL) { //means the user currently has no friends
	friends = make_dictionary(COMPARE_CASE_SENS, NULL);
	dictionary_set(friends, current, NULL); //current as key in inner dictionary
	dictionary_set(serverList, user, friends); //add user with value dictionary in outter dictionary
      }
      else {
	dictionary_set(userFriends, current, NULL);
      }

      if(friendsFriends == NULL) { //means target friend has no friends
	friends = make_dictionary(COMPARE_CASE_SENS, NULL);
	dictionary_set(friends, user, NULL);
	dictionary_set(serverList, current, friends);
      }
      else {
	dictionary_set(friendsFriends, user, NULL);
      }
    }
    count++;
  }

  pthread_mutex_unlock(&mutex); //MUTEX UNLOCK

  dictionary_t* printFriends = dictionary_get(serverList, user); //grab altered value dictionary for user
  if(printFriends != NULL) {
    const char** keys = dictionary_keys(printFriends); //ensure key is freed later
    body = join_strings(keys, '\n');
    len = strlen(body);
    free(keys);
  }
  else {
    len = 0;
    body = NULL;
  }

  /* Send response headers to client */
  header = ok_header(len, "text/html; charset=utf-8");
  Rio_writen(fd, header, strlen(header));
  printf("Response headers:\n");
  printf("%s", header);

  free(header);

  /* Send response body to client */
  Rio_writen(fd, body, len);

  free(friendGroup);
  free(body);
}

/*
 * serve_greet - greet handler
 */
static void serve_greet(int fd, dictionary_t *query)
{
  printf("greeting being served\n\n");

  size_t len;
  char *body, *header;

  char* user = dictionary_get(query, "user");

  body = append_strings("Greetings, ", user, "!", NULL);

  len = strlen(body);

  /* Send response headers to client */
  header = ok_header(len, "text/html; charset=utf-8");
  Rio_writen(fd, header, strlen(header));
  printf("Response headers:\n");
  printf("%s", header);

  free(header);

  /* Send response body to client */
  Rio_writen(fd, body, len);

  free(body);
}

/*
 * clienterror - returns an error message to the client
 */
void clienterror(int fd, char *cause, char *errnum, 
		 char *shortmsg, char *longmsg) 
{
  size_t len;
  char *header, *body, *len_str;

  body = append_strings("<html><title>Friendlist Error</title>",
                        "<body bgcolor=""ffffff"">\r\n",
                        errnum, " ", shortmsg,
                        "<p>", longmsg, ": ", cause,
                        "<hr><em>Friendlist Server</em>\r\n",
                        NULL);
  len = strlen(body);

  /* Print the HTTP response */
  header = append_strings("HTTP/1.0 ", errnum, " ", shortmsg, "\r\n",
                          "Content-type: text/html; charset=utf-8\r\n",
                          "Content-length: ", len_str = to_string(len), "\r\n\r\n",
                          NULL);
  free(len_str);
  
  Rio_writen(fd, header, strlen(header));
  Rio_writen(fd, body, len);

  free(header);
  free(body);
}

static void print_stringdictionary(dictionary_t *d)
{
  int i, count;

  count = dictionary_count(d);
  for (i = 0; i < count; i++) {
    printf("%s=%s\n",
           dictionary_key(d, i),
           (const char *)dictionary_value(d, i));
  }
  printf("\n");
}
