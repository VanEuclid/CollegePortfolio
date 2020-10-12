// Compiling: 
//    module load intel
//    mpicc matmul-assign.c -o matmul
// Executing:
//    mpiexec -n 2 ./matmul
// Sbatch execution:
//    sbatch script.matmul

#include "stdio.h"
#include "mpi.h"

#define N 2
// Can adjust N through #define
//#define N 16
#define P 2

void Calc1(float myc[N/P][N], float mya[N/P][N], float myb[N/P][N], int rank) {
  int i, j, k;
  printf("in Calc1 rank = %d\n", rank);
  for (i = 0; i< N/P; i++) {
    for (j = 0; j<N; j++) {
      myc[i][j] = 0.;
      for (k=0; k<N/P; k++) {
	myc[i][j] += mya[i][rank] * myb[k][j];
	printf("in Rank %d myc[%d][%d] += mya[%d][%d] * myb[%d][%d]\n", rank, i, j, i, rank, k, j);
      }
    }
  }
}

void Calc2(float myc[N/P][N], float mya[N/P][N], float tmpb[N/P][N], int rank) {
  int i, j, k;
  printf("in Calc2 rank = %d\n", rank);
  for (i = 0; i< N/P; i++) {
    for (j = 0; j<N; j++) {
      for (k=0; k<N/P; k++) {
	myc[i][j] += mya[i][(rank+1)%P] * tmpb[k][j];
	printf("in Rank %d myc[%d][%d] += mya[%d][%d] * tmpb[%d][%d]\n", rank, i, j, i, (rank + 1) % P, k, j);
      }
    }
  }
}


void main(int argc, char *argv[]) {
  FILE *f;
  int i, j, k, error, rank, size;
  float a[N][N], b[N][N], c[N][N], myc[N/P][N], tmpb[N/P][N], mya[N/P][N], myb[N/P][N], tmpdata;
  MPI_Request sendreq, rcvreq;
  MPI_Status status;

   MPI_Init(&argc, &argv);
   MPI_Comm_rank( MPI_COMM_WORLD, &rank );
   MPI_Comm_size( MPI_COMM_WORLD, &size );

   printf("rank = %d, size = %d\n", rank, size);

   // sequential MM 
   if (rank == 0) {

     // read in matrix
     f = fopen("matrixA.dat","r");
     for (i = 0; i<N; i++) {   
      for (j = 0; j<N; j++) {   
        error = fscanf(f,"%f",&tmpdata);
        a[i][j] = tmpdata;
        b[i][j] = tmpdata+1.;
      }
     }
     fclose(f);  

     // After computing each point, output sequential results.
     for (i = 0; i< N; i++) {
       for (j = 0; j<N; j++) {
         c[i][j] = 0.;
         for (k=0; k<N; k++) {
           c[i][j] += a[i][k] * b[k][j];
         }
         printf("SEQ: c[%d][%d] = %f\n", i,j,c[i][j]);
        }
     }
   }

   // TODO: Parallel Portion.  Distribute a and b into local copies 
   // mya and myb.  Initialize myc to 0.  Initiate exchange of myb into tmpb.

   MPI_Scatter(a, N*N/P, MPI_FLOAT, mya, N*N/P, MPI_FLOAT, 0, MPI_COMM_WORLD);
   MPI_Scatter(b, N*N/P, MPI_FLOAT, myb, N*N/P, MPI_FLOAT, 0, MPI_COMM_WORLD);
   MPI_Isend(myb, N*N/P, MPI_FLOAT, (rank + 1) % P, 0, MPI_COMM_WORLD, &sendreq);
   MPI_Irecv(tmpb, N*N/P, MPI_FLOAT, (rank + P - 1) % P, 0, MPI_COMM_WORLD, &rcvreq);

   // TODO: Implement Calc1.  Should be like sequential code, but use
   // myc, mya, and myb.  Adjust bounds for loops corresponding to rows 
   // due to partitioning (i and k).  Index mya appropriately based on 
   // portion of tmpb to be computed.
   Calc1(myc, mya, myb, rank);

   // TODO: Make sure you have received tmpb.
   MPI_WAIT(&rcvreq, &status);

   // TODO: Implement Calc2.  Should be like sequential code, but use
   // myc, mya, and tmpb.  Adjust bounds for loops corresponding to rows 
   // due to partitioning (i and k).  Index mya appropriately based on 
   // portion of tmpb to be computed.
   Calc2(myc, mya, tmpb, rank);
   
   // Output local results to compare against sequential
   for (i = 0; i<N/P; i++) {   
      for (j = 0; j<N; j++) {   
	printf("PAR, RANK %d: c[%d][%d] = %f\n", rank, i+rank*N/P,j,myc[i][j]);
      }
   }

   MPI_Finalize();
}
