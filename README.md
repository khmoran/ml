#################################
#        Detect Outliers        #
#################################

Usage
-----
Usage: detect-outliers <dataset1> <dataset2> [threshold]
       OR
       run <dataset1> <dataset2> [threshold]
Detect outliers from <dataset1> and <dataset2> using two methods: k-means and COD (in this case, k-medoids-based). Output will be written to k-means-dataset1.dat, k-means-dataset2.dat, cod-dataset1.dat, and cod-dataset2.dat.

ex.:

$ ./detect-outliers Dataset1-outlier.arff Dataset2-outlier.arff

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.cluster.outlier.drivers.DetectOutliers <dataset1> <dataset2> [threshold]


#################################
#      Cluster with k-Means     #
#################################

Usage
-----
Usage: cluster <dataset> [out]
Clusters features from <dataset> using two initialization methods-- random and density-based-- and outputs the statistical results of each into [out] or, if [out] is not provided, the default file: kmeans.out.

ex.:

$ ./cluster segment-full.arff

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.cluster.drivers.ClusterWithKMeans <dataset> [out]


#################################
#        Active Learning        #
#################################

Usage
-----
Usage: active-learn <train> <out-random> <out-uncertainty>
Learns features from <train> using two methods-- random and uncertainty sampling-- and outputs the results of each to their respective output files.

ex.:

$ ./active-learn train.arff random.out uncertainty.out

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.learning.active.drivers.ActiveLearn <train> <out-random> <out-uncertainty>

Note: you can also run the active learner for 10 iterations at a time by invoking this class:

$ java -cp target/classes edu.tufts.cs.ml.learning.active.drivers.BatchActiveLearn <train> <out-random> <out-uncertainty>


#################################
#        Select Features        #
#################################

Usage
-----
Usage: select-features <train> <out-filter> <out-wrapper> <out-own>
Selects features from <train> using three methods-- Filter, Wrapper, and my own combination-- and outputs the results of each to their respective output files.

ex.:

$ ./select-features train.arff filter.out wrapper.out own.out

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.features.drivers.SelectFeatures <train> <out-filter> <out-wrapper> <out-own>

#################################
#    Validate kNN with LOOCV    #
#################################

Usage
-----
Usage: validate <train> [out] [--normalize]
Validates <train> using LOOCV and, if normalizing, outputs normalized data to [out].

ex.:

$ ./validate train.arff out.arff --normalize

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.validate.drivers.Validate <train> [out] [--normalize]

#################################
#  Missing Values Preprocessor  #
#################################

Usage
-----
Usage: preprocess <train> <test> <out-train> <out-test> <method>
Fills in missing values in <train> and <test> based on the data in <train> and the method <method>.
Valid values for <method> are: {MEAN, MEDIAN, MEAN_SAME_CLASS}

ex.:

$ ./preprocess train.arff test.arff output_train.arff output_test.arff MEAN

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.preprocess.drivers.ProcessMissingValues <train> <test> <out-train> <out-test> <method>

#################################
# k-Nearest Neighbor Classifier #
#################################

Usage
-----
Usage: classify <train> <test> <out> [--normalize]
Trains on TRAIN, classifies TEST, and writes results to OUT.

ex.:

$ ./classify train.arff test.arff output.arff --normalize

Alternatively:

$ java -cp target/classes edu.tufts.cs.ml.classify.drivers.ClassifyWithKNN <train> <test> <out> [--normalize]

Contact
-------
Kelly Moran
kmoran@cs.tufts.edu