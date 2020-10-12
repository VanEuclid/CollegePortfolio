#include "stdio.h"
#include "mpi.h"

#define N 9
#define P 9
#define SQRP 3

void
main(int argc, char *argv[]) {
  FILE *f;
  int i, j, k, error, rank, size;
  float a[N][N], b[N][N], c[N][N], myc[SQRP][SQRP], mya[SQRP][SQRP], myb[SQRP][SQRP], tmpdata;
  MPI_Request sendreq, rcvreq;
  MPI_Status status;

  MPI_Init(&argc, &argv);
  MPI_Comm_rank( MPI_COMM_WORLD, &rank );
  MPI_Comm_size( MPI_COMM_WORLD, &size );

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
	  //if(rank == 0 && i == 0 && j == 0) {
	    //printf("calculating from rank = %d :: c[%d][%d] = %f += a[%d][%d] = %f * b[%d][%d] = %f\n", rank, i, j, c[i][j], i, k, a[i][k], k, j, b[k][j]);
	  //}
	  c[i][j] += a[i][k] * b[k][j];
	}
	printf("SEQ: c[%d][%d] = %f\n", i,j,c[i][j]);
      }
    }
  }

  // TODO: Parallel Portion.  Distribute a and b into local copies 
  // mya and myb using Scatterv as in website pointed to by Lecture 21.  
  // Initialize myc to 0.  
  MPI_Datatype block, blocktype;
  int displ[] = {0, 1, 2, 9, 10, 11, 18, 19, 20};;
  int scount[] = {1, 1, 1, 1, 1, 1, 1, 1, 1};
  MPI_Type_vector(3, 3, 9, MPI_FLOAT, &block);
  MPI_Type_create_resized(block, 0, 3*sizeof(float), &blocktype);
  MPI_Type_commit(&blocktype);
  MPI_Scatterv(c, scount, displ, blocktype, myc, P, MPI_FLOAT, 0, MPI_COMM_WORLD);
  MPI_Scatterv(a, scount, displ, blocktype, mya, P, MPI_FLOAT, 0, MPI_COMM_WORLD);
  MPI_Scatterv(b, scount, displ, blocktype, myb, P, MPI_FLOAT, 0, MPI_COMM_WORLD);

  //MPI_Datatype col, coltype;
  //MPI_Type_vector(9, 1, 9, MPI_FLOAT, &col);
  //MPI_Type_create_resized(col, 0, 1*sizeof(float), &coltype);
  //MPI_Type_commit(&coltype);
  //MPI_Scatter(b, 1, coltype, myb, P, MPI_FLOAT, 0, MPI_COMM_WORLD);
  //MPI_Scatter(a, 9, MPI_FLOAT, mya, P, MPI_FLOAT, 0, MPI_COMM_WORLD);

  for(i = 0; i < SQRP; i++) {
    for(j = 0; j < SQRP; j++) {
      myc[i][j] = 0.;
    }
  }

  // TODO: Now create Cartesian grid communicator (see website pointed to
  // by Lecture 21 and Sundar-communicators.pdf on Canvas)
  int dims[2] = {3, 3};
  int periods[2] = {1, 1};
  MPI_Comm c_comm;
  MPI_Cart_create(MPI_COMM_WORLD, 2, dims, periods, 0, &c_comm);

  //int dimsr[2] = {9, 1};
  //MPI_Comm row_comm;
  //MPI_Cart_create(MPI_COMM_WORLD, 2, dimsr, periods, 0, &row_comm);

  //int dimsc[2] = {1, 9};
  //MPI_Comm col_comm;
  //MPI_Cart_create(MPI_COMM_WORLD, 2, dimsc, periods, 0, &col_comm);

  // TODO: Move a and b data within Cartesian Grid using initial skew 
  // operations (see p. 10 of Lecture 20.)

  //for(i = 0; i<N; i++) {
    //printf("rank = %d, mya[0][%d] = %f AND myb[0][%d] = %f\n", rank, i, mya[0][i], i, myb[0][i]);
  //}
  //if(rank == 0) {
  //  for(i = 0; i<N; i++) {
  //    for(j = 0; j<N; j++) { 
  //	printf("rank = %d, a[%d,%d] = %f, b[%d,%d] = %f\n", rank, i, j, a[i][j], i, j, b[i][j]);
  //  }
  //}
  //}

  int coords[2];
  int right, left, down, up, c_comm_rank;
  MPI_Comm_rank(c_comm, &c_comm_rank);
  MPI_Cart_coords(c_comm, c_comm_rank, 2, coords);
  MPI_Cart_shift(c_comm, 1, coords[0], &left, &right);
  MPI_Cart_shift(c_comm, 0, coords[1], &up, &down);
  //printf("rank = %d / c_comm_rank = %d, coords[0] = %d, coords[1] = %d, left = %d right = %d, up = %d down = %d\n", rank, c_comm_rank, coords[0], coords[1], left, right, up, down);
  MPI_Sendrecv_replace(mya, 9, MPI_FLOAT, left, 30, right, 30, c_comm, MPI_STATUS_IGNORE);
  MPI_Sendrecv_replace(myb, 9, MPI_FLOAT, up, 30, down, 30, c_comm, MPI_STATUS_IGNORE);

  int shifts;
  // TODO: Add following loop:
  for(shifts = 0; shifts<=SQRP-1; shifts++){
    for(i=0; i<SQRP; i++) {
      for(j=0; j<SQRP; j++) {
	for (k=0; k<SQRP; k++) {
  //    CALC: Should be like sequential code, but use
  //          myc, mya, and myb.  Adjust bounds for all loops to SQRP.  
  //          (More generally, (N/P/SQRP)).
  //    SHIFT: Shift A leftward and B upward by 1 in Cartesian grid.

	  //if(rank == 0 && i == 0 && j == 0) {
	    //printf("calculating from rank = %d :: myc[%d][%d] = %f += mya[%d][%d] = %f * myb[%d][%d] = %f\n", rank, i, j, myc[i][j], i, k, mya[i][k], k, j, myb[k][j]);
	  //}
	  myc[i][j] += mya[i][k] * myb[k][j];
	}
      }
    }
    MPI_Cart_shift(c_comm, 1, 1, &left, &right);
    MPI_Cart_shift(c_comm, 0, 1, &up, &down);
    MPI_Sendrecv_replace(&mya, 9, MPI_FLOAT, left, 30, right, 30, c_comm, MPI_STATUS_IGNORE);
    MPI_Sendrecv_replace(&myb, 9, MPI_FLOAT, up, 30, down, 30, c_comm, MPI_STATUS_IGNORE);
  }
 
  // Output local results to compare against sequential
  for (i = 0; i<SQRP; i++) {
    for (j = 0; j<SQRP; j++) {   
      printf("PAR, RANK %d: c[%d][%d] = %f\n", rank, (rank/SQRP)+i,(rank % SQRP)+j,myc[i][j]);
    }
  }

  MPI_Finalize();
}
