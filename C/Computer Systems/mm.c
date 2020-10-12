 /*
 * mm-naive.c - The least memory-efficient malloc package.
 * 
 * In this naive approach, a block is allocated by allocating a
 * new page as needed.  A block is pure payload. There are no headers or
 * footers.  Blocks are never coalesced or reused.
 *
 * NOTE TO STUDENTS: Replace this header comment with your own header
 * comment that gives a high level description of your solution.
 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <unistd.h>
#include <string.h>

#include "mm.h"
#include "memlib.h"

/* always use 16-byte alignment */
#define ALIGNMENT 16

/* rounds up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~(ALIGNMENT-1))

/* rounds up to the nearest multiple of mem_pagesize() */
#define PAGE_ALIGN(size) (((size) + (mem_pagesize()-1)) & ~(mem_pagesize()-1))

/* gives int size of overhead of both header and footer */
#define OVERHEAD (sizeof(block_header) + sizeof(block_footer))

/* given a pointer to a header, get or set its value (assuming size_t) */
#define GET(p) (*(size_t *)(p)) //GETS EVERYTHING IN HEADER INCLUDING ALLOC AND SIZE
#define PUT(p, val) (*(size_t *)(p) = (val))

/* given a payload pointer, get the header or footer pointer */
#define HDRP(bp) ((char*)(bp) - sizeof(block_header))
#define FTRP(bp) ((char*)(bp)+GET_SIZE(HDRP(bp))-OVERHEAD)

/* given a header pointer, get the alloc or size */
//#define GET_ALLOC(p) ((block_header*)(p))->allocated
//#define GET_SIZE(p) ((block_header*)(p))->size
#define GET_ALLOC(p) (GET(p) & 0x1)
#define GET_SIZE(p) (GET(p) & ~0xF)

// Combine a size and alloc bit
#define PACK(size, alloc) ((size) | (alloc))

/* given a payload pointer, get the next or previous payload pointer */
#define NEXT_BLKP(bp) ((char*)(bp) + GET_SIZE(HDRP(bp)))
#define PREV_BLKP(bp) ((char*)(bp)-GET_SIZE((char*)(bp)-OVERHEAD))

//typedef struct { //16 BYTE HEADERS AND FOOTERS
//  size_t size;
//  char allocated;
//} block_header;

//typedef struct {
//  size_t size;
//  char filler;
//} block_footer;

typedef struct list_node { //EXPLICIT FREE LIST
  struct list_node* prev;
  struct list_node* next;
} list_node;

typedef size_t block_header; //8 BYTE HEADERS AND FOOTERS
typedef size_t block_footer;

static list_node* first_free;
void *current_avail = NULL; //USED FOR ONLY INITILIAZATION AND EXTEND, FREE LIST HANDLES AVAILABLE
int current_avail_size = 0;
static int overallHeapSize = 0; //OVERALL SIZE OF COMBINED PAGES

////////////////////////////////////////////////LIST STUFF//////////////////////////////////////////
void add_node(void* bp) {
  list_node* new = (list_node*)bp;
  first_free->prev = new;
  new->next = first_free;
  new->prev = NULL;
  first_free = new;
}

void delete_node(void* bp) {
  list_node* target = (list_node*)bp;
  if(target->prev == NULL && target->next != NULL) { //HEADER EDGE
    //printf("first edge delete\n");
    first_free = target->next;
    target->next->prev = NULL;
  }
  else if(target->next == NULL && target->prev != NULL) { //LAST EDGE
    //printf("last edge delete\n");
    target->prev->next = NULL;
  }
  else { //MIDDLE
    target->prev->next = target->next;
    target->next->prev = target->prev;
  }
}

list_node* showExplicitFreeList(list_node* bp) {
  printf("tree node = %p, first_free = %p, node header = %p, node footer = %p, node size = %zu\n", bp, first_free, HDRP(bp), FTRP(bp), GET_SIZE(HDRP(bp)));
  if(bp->next == NULL)
    return NULL;
  return showExplicitFreeList(bp->next);
}

list_node* searchExplicitFreeList(list_node* bp, size_t amt) { //typically for allocation
  char* header = HDRP(bp); //current node header
  //printf("SEARCH NODE: tree node = %p, size of block node = %zu\n", bp, GET_SIZE(header));

  if(GET_SIZE(header) >= amt) {
    return bp;
  }

  if(bp->next == NULL) //means that this is the last free node and nothing fits on free list.
    return NULL;
  else 
    return searchExplicitFreeList(bp->next, amt);
}
////////////////////////////////////////////LIST STUFF///////////////////////////////////////////

static int counter;
//sums up the free list node sizes
list_node* sumAvailable(list_node* node) {
  counter += GET_SIZE(HDRP(node));
  if(node->next == NULL)
    return NULL;
  return sumAvailable(node->next);
}

/*
 * What it does:
 * 1. Check if current_avail_size == what is free in list
 */
static void heapConsistencyChecker() {
  counter = 0;
  sumAvailable(first_free);
  if(counter != current_avail_size) {
    showExplicitFreeList(first_free);
    printf("currentavail = %d, counter = %d\n", current_avail_size, counter);
    printf("---ERROR free list size and current avail size dont not match---\n");
    exit(-1);
  }
}

/*
 * Request more memory by calling mem_map
 * Initialize the new chunk of memory as applicable
 * Update free list if applicable
 */
static void extend(size_t size) {
  //printf("extend being called\n");

  int numberofBytes = 0;

  //int numberofBytes = PAGE_ALIGN(size); //34/100
  //if(size >= 1 && size <= 4096) //1 page
  //  numberofBytes = PAGE_ALIGN(size) * 2;
  //  numberofBytes += PAGE_ALIGN(size);
  //else if(size > 4096 && size <= 8192) //2 pages
  //  numberofBytes = PAGE_ALIGN(size) + PAGE_ALIGN(1);
  //if(size > 8192) //n page
  //  numberofBytes = PAGE_ALIGN(size) + (PAGE_ALIGN(1)*2);

  //int currentHeapDoubled = overallHeapSize * 2; //62/100
  //int currentHeapPlusSize = overallHeapSize + PAGE_ALIGN(size);
  //printf("%d, %d\n",currentHeapDoubled, currentHeapPlusSize);
  //while(1) {
  //  if(currentHeapPlusSize > currentHeapDoubled) {
      //printf("doubling\n");
  //    currentHeapDoubled = currentHeapDoubled * 2;
  //  } else {
  //    break;
  //  }
  //}
  //numberofBytes = currentHeapDoubled - overallHeapSize;

  numberofBytes = PAGE_ALIGN(size)*15; //73/100
  //printf("# of pages = %zu\n", (numberofBytes/(PAGE_ALIGN(1))));

  current_avail_size += numberofBytes;
  //printf("newHeapSize = %d, current_avail_size = %d, page aligned size = %zu\n", overallHeapSize, current_avail_size, PAGE_ALIGN(size));

  //int allocatedSize = PAGE_ALIGN(size); //USED FOR MOVING POINTER/PLACEMENT 
  int allocatedSize = numberofBytes;

  current_avail = mem_map(allocatedSize);
  overallHeapSize = mem_heapsize();
  //printf("what extend new map points to = %p\n", current_avail);

  block_header* chunkHeader = current_avail;
  PUT(chunkHeader, PACK(allocatedSize - 32, 1));

  allocatedSize -= sizeof(block_header);
  current_avail_size -= sizeof(block_header);
  current_avail += sizeof(block_header);

  block_header* senthead = current_avail; //16 byte headers
  //PUT(senthead, 32);
  //senthead->allocated = 1;
  PUT(senthead, PACK(16, 1));

  current_avail += sizeof(block_header); //move pointer to where sent footer goes 

  block_footer* sentfoot = current_avail;
  //PUT(sentfoot, 32);
  PUT(sentfoot, PACK(16, 1));

  current_avail_size -= OVERHEAD; //subtract overhead of new page
  allocatedSize -= OVERHEAD;

  current_avail += sizeof(block_footer); //move pointer to where payload header needs to be

  //block_header* terminator = (current_avail + (allocatedSize - 16));
  block_header* terminator = (current_avail + (allocatedSize - 8));
  //PUT(terminator, 0);
  //terminator->allocated = 1;
  PUT(terminator, PACK(0, 1));

  current_avail_size -= sizeof(block_header); //subtract overhead of term
  allocatedSize -= sizeof(block_header);

  block_header* payloadHeader = current_avail;
  //PUT(payloadHeader, allocatedSize);
  //payloadHeader->allocated = 0;
  PUT(payloadHeader, PACK(allocatedSize, 0));

  block_footer* payloadFooter = (current_avail + (allocatedSize - 16));
  //PUT(payloadFooter, allocatedSize);
  PUT(payloadFooter, PACK(allocatedSize, 0));

  current_avail += sizeof(block_header); //move current pointer to where the free payload is

  add_node(current_avail); //add fresh page to list
}

/*
 * Set a block to allcoated
 * Update block headers/footers as needed
 * Update free list if applicable
 * Split block if applicable
 */
static void set_allocated(void* b, size_t size) {
  char* allocationHeader = HDRP(b); //allocation header
  //GET_ALLOC(allocationHeader) = 1;
  PUT(allocationHeader, PACK(GET_SIZE(allocationHeader), 1));
  
  int newFreeBlockSize = GET_SIZE(allocationHeader) - size; //size of leftover after split consider
  //printf("remainingsize = %d, allocationsize = %zu, before split alloHeader = %p, allocation at = %p\n", newFreeBlockSize, size, allocationHeader, b);

  if(newFreeBlockSize < (ALIGN(1) + OVERHEAD)) { //block split would make invalid size block
    //printf("allocating entire block\n");
    current_avail_size -= GET_SIZE(allocationHeader);
    if(current_avail_size == 0) { 
      //printf("current size is 0, extend being called\n");
      extend(size);
    }
    delete_node(b);
  } 
  else { //splitting block
    //printf("splitting block\n");
    //PUT(allocationHeader, size);
    PUT(allocationHeader, PACK(size, 1));
    char* allocationFooter = FTRP(b); //allocation footer will now be placed correctly because size is PUT in header
    //PUT(allocationFooter, size);
    PUT(allocationFooter, PACK(size, 1));

    char* nextFreePayload = NEXT_BLKP(b); //NEXT_BLKP SEEMS WEIRD HERE
    //char* nextFreePayload = allocationFooter + 32;

    char* nextFreeHeader = HDRP(nextFreePayload); //free header
    //PUT(nextFreeHeader, newFreeBlockSize); //place leftover size in header
    //GET_ALLOC(nextFreeHeader) = 0;
    PUT(nextFreeHeader, PACK(newFreeBlockSize, 0));

    void* nextFreeFooter = FTRP(nextFreePayload); //free footer
    //PUT(nextFreeFooter, newFreeBlockSize);
    PUT(nextFreeFooter, PACK(newFreeBlockSize, 0));

    add_node(nextFreePayload);
    delete_node(b);
    
    current_avail_size -= GET_SIZE(allocationHeader); //remove free bytes after finished allocation
  }
}

/*
 * Coalesce a freeblock if applicable
 * Returns pointer to new coalesced block
 */
static void* coalesce(void* bp) {
  char* header = HDRP(bp);

  char* leftbp = PREV_BLKP(bp); //Left payload
  char* rightbp = NEXT_BLKP(bp); //Right payload
  void* leftHeader = HDRP(leftbp);
  void* rightHeader = HDRP(rightbp);
  //printf("left then right = %p, %p\n", leftbp, rightbp);

  int leftAlloc = GET_ALLOC(leftHeader);
  int rightAlloc = GET_ALLOC(rightHeader);
  //printf("left and right alloc = %d, %d, size = %zu, %zu\n", leftAlloc, rightAlloc, GET_SIZE(leftHeader), GET_SIZE(rightHeader));

  int sizebp = GET_SIZE(header); //For size changes when combining blocks.
  
  if(leftAlloc == 1 && rightAlloc == 1) { //CASE 1 neither left or right
    //printf("case 1\n");
    add_node(bp);
    return bp;
  }
  else if(leftAlloc == 0 && rightAlloc == 1) { //CASE 2 left free
    //printf("case 2\n");
    char* newHeader = HDRP(leftbp); 
    char* newFooter = FTRP(bp);
    int combinedSize = GET_SIZE(newHeader) + sizebp;
    //PUT(newHeader, combinedSize);
    //PUT(newFooter, combinedSize);
    PUT(newHeader, PACK(combinedSize, 0));
    PUT(newFooter, PACK(combinedSize, 0));
    return leftbp;
  }
  else if(leftAlloc == 1 && rightAlloc == 0) { //CASE 3 right free
    //printf("case 3\n");
    char* newHeader = HDRP(bp);
    char* newFooter = FTRP(rightbp);
    int combinedSize = GET_SIZE(newFooter) + sizebp;
    //printf("right size = %zu, current size = %zu\n", GET_SIZE(newFooter), GET_SIZE(newHeader));
    //PUT(newHeader, combinedSize);
    //PUT(newFooter, combinedSize);
    PUT(newHeader, PACK(combinedSize, 0));
    PUT(newFooter, PACK(combinedSize, 0));
    add_node(bp);
    delete_node(rightbp);
    return bp;
  }
  else if (leftAlloc == 0 && rightAlloc == 0) { //CASE 4 both free
    //printf("case 4\n");
    char* leftHeader = HDRP(leftbp);
    char* rightFooter = FTRP(rightbp);
    int combinedSize = GET_SIZE(rightFooter) + GET_SIZE(leftHeader) + sizebp;
    //PUT(leftHeader, combinedSize);
    //PUT(rightFooter, combinedSize);
    PUT(leftHeader, PACK(combinedSize, 0));
    PUT(rightFooter, PACK(combinedSize, 0));
    delete_node(rightbp);
    return leftbp;
  }
  else {
    return NULL;
  }
}

/* 
 * mm_init - initialize the malloc package.
 */
int mm_init(void)
{
  //printf("\n-----mm_init-----\n");
  
  current_avail_size = PAGE_ALIGN(1) * 2; //4096 to start 61/100
  //printf("page size is = %d\n", current_avail_size);

  current_avail = mem_map(current_avail_size); //MAP MEMORY!
  overallHeapSize = mem_heapsize(); 
  //printf("pointer to start of page = %p (from current)\n", current_avail);

  block_header* chunkHeader = current_avail; //used for unchunking
  PUT(chunkHeader, PACK(current_avail_size - 32, 1));
  //printf("chunkHeader size = %zu, chunkHeader alloc = %zu, where chunkHeader points = %p\n", GET_SIZE(chunkHeader), GET_ALLOC(chunkHeader), chunkHeader);

  current_avail_size -= sizeof(block_header);
  current_avail += sizeof(block_header); //move current to sent header placement

  block_header* sentHeaderPtr = current_avail; //sent header
  PUT(sentHeaderPtr, PACK(16, 1));
  //PUT(sentHeaderPtr, 32);
  //sentHeaderPtr->allocated = 1;
 
  //if(sentHeaderPtr != current_avail && GET(current_avail) != 32)
  //  return -1;
  if(sentHeaderPtr != current_avail && GET_SIZE(current_avail) != 16)
    return -1;

  current_avail += sizeof(block_header); //move current to sent footer placement
  //printf("moving current for sentFooter = %p\n", current_avail);

  block_footer* sentFooterPtr = current_avail;
  PUT(sentFooterPtr, PACK(16, 1));
  //PUT(sentFooterPtr, 32);

  //if(sentFooterPtr != current_avail && GET(current_avail) != 32)
  //  return -1;
  if(sentFooterPtr != current_avail && GET(current_avail) != 16)
    return -1;

  current_avail_size -= OVERHEAD;
  //printf("current size = %d\n", current_avail_size);

  current_avail += sizeof(block_footer); //moving current to where payload needs to go
  //block_header* terminatorPtr = (current_avail + (current_avail_size - 16));
  block_header* terminatorPtr = (current_avail + (current_avail_size - 8));
  //printf("where terminator points to = %p, where current points to = %p (should still be where the payload needs to be)\n", terminatorPtr, current_avail);
  PUT(terminatorPtr, PACK(0, 1));
  //PUT(terminatorPtr, 0);
  //terminatorPtr->allocated = 1;

  current_avail_size -= sizeof(block_header);

  block_header* payloadHeader = current_avail;
  //PUT(payloadHeader, current_avail_size);
  //payloadHeader->allocated = 0;
  PUT(payloadHeader, PACK(current_avail_size, 0));

  block_footer* payloadFooter = (current_avail + (current_avail_size - 8));
  //PUT(payloadFooter, current_avail_size);
  PUT(payloadFooter, PACK(current_avail_size, 0));

  current_avail += sizeof(block_header); //moves current to the first unallocated block payload

  if((block_footer*)payloadFooter != (block_footer*)(terminatorPtr - 1)) //FROM TERMINATOR MOVE ONE 16 BYTE BLOCK LEFT FOR PAYLOAD FOOTER CHECK
    return -1;
  if(GET(payloadHeader) != current_avail_size && (block_header*)HDRP(current_avail) != payloadHeader)
    return -1;
  if(GET(payloadFooter) != current_avail_size && (block_footer*)FTRP(current_avail) != payloadFooter)
    return -1;
  
  first_free = current_avail; //First node in explicit free list
  first_free->prev = NULL;
  first_free->next = NULL;
  return 0;
}

/* 
 * mm_malloc - Allocate a block by using bytes from current_avail,
 *     grabbing a new page if necessary.
 */
void *mm_malloc(size_t size)
{
  //printf("\nmm_malloc called\n");

  int newsize = ALIGN(size); //align allocation size payload
  newsize += OVERHEAD; //add overhead footer and header
  //printf("allocation = %d, current_avail_size = %d\n", newsize, current_avail_size);
  void *p; //payload to return
  if (current_avail_size < newsize) {
    //printf("extend seen at start of malloc\n");
    extend(newsize);
  }

  //list_node* targetNode = searchExplicitFreeList(first_free, newsize); //search for block that fits
  list_node* targetNode = first_free;
  while(1) {
    if(GET_SIZE(HDRP(targetNode)) >= newsize)
      break;
    if(targetNode->next == NULL) {
      targetNode = NULL;
      break;
    }

    targetNode = targetNode->next;
  }

  if(targetNode == NULL) { //when malloc does not fit in any block in free list
    //printf("extend seen from target node is NULL, free_list has no free blocks large enough for size\n");
    extend(newsize);
    targetNode = first_free;
    set_allocated((void*)targetNode, newsize);
    p = targetNode;
  }
  else { //regular allocate
    set_allocated((void*)targetNode, newsize);
    p = targetNode;
  }

  //printf("currensize after allocation = %d, heapsize = %d\n", current_avail_size, overallHeapSize);
  //heapConsistencyChecker();
  return p;
}

/*
 * mm_free - Freeing a block coalesces and unmaps completely empty chunks
 */
void mm_free(void *ptr) {
  //printf("\npointer being FREEd = %p, size = %zu\n", ptr, GET_SIZE(HDRP(ptr)));

  //GET_ALLOC(HDRP(ptr)) = 0; //deallocate ptr
  PUT(HDRP(ptr), PACK(GET_SIZE(HDRP(ptr)), 0));
  current_avail_size += GET_SIZE(HDRP(ptr));
  char* coalescedPtr = coalesce(ptr); //use coalesced ptr to unmap chunks

  //printf("overallHeap before unmap = %d\n", overallHeapSize);
  if(overallHeapSize > (PAGE_ALIGN(1) * 2)) { //CHUNK UNMAPPING, need a lowerbound so that we do not unmap to often (leads to more mapping)
    //printf("considering unmapping\n");
    char* looksentHead = HDRP(coalescedPtr) - 8;
    char* looktermHead = FTRP(coalescedPtr) + 8;
    size_t sizetoRelease = GET_SIZE(HDRP(coalescedPtr));
    size_t pagesize = GET_SIZE(HDRP(looksentHead - 8));
    //printf("pointers = %p, %p, %p\n", HDRP(coalescedPtr), coalescedPtr, FTRP(coalescedPtr));
    //printf("looksent = %p, lookterm = %p, coal = %p, sizeofcoal = %zu == pagesize = %zu\n", looksentHead, looktermHead, coalescedPtr, sizetoRelease, pagesize);

    int sentalloc = GET_ALLOC(looksentHead);
    size_t sentsize = GET_SIZE(looksentHead);
    int termalloc = GET_ALLOC(looktermHead);
    int termsize = GET_SIZE(looktermHead);

    int heapsizeAfterUnmap = overallHeapSize - (sizetoRelease+32);
    //printf("heapAfterUnmap = %d\n", heapsizeAfterUnmap);

    //printf("if statement = %d, %zu, %d, %d\n", sentalloc, sentsize, termalloc, termsize);
    if(sentalloc == 1 && sentsize == 16 && termalloc == 1 && termsize == 0 && sizetoRelease == pagesize && heapsizeAfterUnmap != 0) { //umap chunk
      //printf("unmapping\n");
      delete_node(coalescedPtr);
      mem_unmap(coalescedPtr - 32, pagesize + 32);
      overallHeapSize = mem_heapsize();
      current_avail_size -= (pagesize);
    }
  }

  //printf("heapsize = %d, currentsize = %d\n", overallHeapSize, current_avail_size);
  //heapConsistencyChecker();
}
