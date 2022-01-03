package com.suez.uni.petroleum.engineering.production.calculator;

import android.util.Log;

public class Ipr {

    private double prAverage = 0;
    private double pb = 0;
    private double pwfTable = 0;
    private double pwf1 = 0;
    private double pwf2 = 0;
    private double qlTable = 0;
    private double ql1 = 0;
    private double ql2 = 0;
    private double fe = 0;
    private double s = 0;




    public static double getQMax(double qlTest1, double pwfTest1, double prAverage) {

        double qMax = qlTest1 / (1 - 0.2 * (pwfTest1 / prAverage) - 0.8*(Math.pow(pwfTest1 / prAverage, 2)));

        return qMax;

    }


    /**
     * for saturated without fe.
     *
     * @param qlTest
     * @param pwfTest
     * @param prAverage
     * @param pwfTable
     * @return
     */
    public static double getQTableSaturated(double qlTest, double pwfTest, double prAverage, double pwfTable) {

        double qMax = qlTest / (1 - 0.2 * (pwfTest / prAverage) - 0.8*(Math.pow(pwfTest / prAverage, 2)));

        double qTable = qMax * (1 - 0.2*(pwfTable / prAverage) - 0.8*(Math.pow(pwfTable / prAverage, 2)));

        return qTable;

    }


    /**
     * for under saturated without fe.
     *
     *
     * @param qlTest
     * @param pwfTest
     * @param prAverage
     * @param pwfTable
     * @param pb
     * @return
     */
    public static double getQTableUnderSaturatedCasePwfBiggerOrEqualPb(double qlTest, double pwfTest,
                                                                double prAverage, double pwfTable,
                                                                double pb) {

        double j = qlTest / (prAverage - pwfTest);

        double qb = j * (prAverage - pb);

        double qTable;
        if (pwfTable >= pb) {
            qTable = j * (prAverage - pwfTable);
        } else {
            qTable = qb + (j*pb / 1.8)*(1-0.2*(pwfTable/pb) - 0.8*Math.pow((pwfTable/pb) ,2));
        }

        return qTable;

    }


    /**
     * for under saturated without fe.
     *
     * @param qlTest
     * @param pwfTest
     * @param prAverage
     * @param pwfTable
     * @param pb
     * @return
     */
    public static double getQTableUnderSaturatedCasePwfSmallerPb(double qlTest, double pwfTest,
                                                                double prAverage, double pwfTable,
                                                                double pb) {

        double j = qlTest / (prAverage - pb + (pb/1.8) * (1-0.2*(pwfTest/pb) - 0.8*Math.pow((pwfTest/pb), 2)) );

        double qb = j * (prAverage - pb);


        double qTable;
        if (pwfTable >= pb) {
            qTable = j * (prAverage - pwfTable);
        } else {
            qTable = qb + (j*pb / 1.8)*(1-0.2*(pwfTable/pb) - 0.8*Math.pow((pwfTable/pb) ,2));
        }

        return qTable;

    }



    public static double getQTableSaturatedWithFe(double qlTest1, double pwfTest1, double prAverage,
                                            double pwfTable, double feTest, double fe) {

//        if(feTest == -1) {
//            feTest = getFe(qlTest1, pwfTest1, prAverage, s, qlTest2, pwfTest2);
//        }

        double qMax = qlTest1 / (1.8 * feTest * (1- (pwfTest1 / prAverage)) - 0.8*(Math.pow(1- (pwfTest1 / prAverage), 2) * Math.pow(feTest, 2)));

        double qTable = qMax * (1.8 * fe * (1- (pwfTable / prAverage)) - 0.8*(Math.pow(1- (pwfTable / prAverage), 2) * Math.pow(fe, 2)));

        if (qTable > qMax) {
            return -1.0;
        }

        return qTable;

    }


    public static double getQTableUnderSaturatedCasePwfBiggerOrEqualPbWithFe(double qlTest1, double pwfTest1, double prAverage,
                                            double pwfTable, double pb, double feTest, double fe) {

//        if(fe == -1) {
//            fe = getFe(qlTest1, pwfTest1, prAverage, s, qlTest2, pwfTest2);
//        }

        double j = qlTest1 / (prAverage - pwfTest1);

        if (feTest != fe) {
            j = j * fe / feTest;
        }

        double qTable;
        if (pwfTable >= pb) {
            qTable = j * (prAverage - pwfTable);
        } else {
            qTable = j * (prAverage - pb) + j*pb/1.8 * (1.8*(1-(pwfTable/pb)) - 0.8*fe*Math.pow(1-(pwfTable/pb), 2));
        }

        return qTable;

    }


    public static double getQTableUnderSaturatedCasePrSmallerPbWithFe(double qlTest1, double pwfTest1, double prAverage,
                                                               double pwfTable, double pb, double feTest, double fe) {

//        if(fe == -1) {
//            fe = getFe(qlTest1, pwfTest1, prAverage, s, qlTest2, pwfTest2);
//        }

        double j = qlTest1 / ((prAverage - pb) + (pb/1.8)*(1.8*(1-pwfTest1/pb))-0.8*feTest*Math.pow(1-pwfTest1/pb, 2));

        if (feTest != fe) {
            j = j * fe / feTest;
        }


        double qTable;
        if (pwfTable >= pb) {
            qTable = j * (prAverage - pwfTable);
        } else {
            qTable = j * (prAverage - pb) + j*pb/1.8 * (1.8*(1-(pwfTable/pb)) - 0.8*fe*Math.pow(1-(pwfTable/pb), 2));
        }


        return qTable;

    }


    public static double getPwfMinimum(double qlTest1, double pwfTest1, double prAverage,
                                       double fe, double s, double qlTest2, double pwfTest2) {

        if(fe == -1) {
            fe = getFe(qlTest1, pwfTest1, prAverage, s, qlTest2, pwfTest2);
        }


        double pwfMinimum = prAverage * (1-(1/fe));

        return pwfMinimum;

    }


    public static double getQMaxActual(double qlTest1, double pwfTest1, double prAverage,
                                       double feTest, double fe) {

//        if(fe == -1) {
//            fe = getFe(qlTest1, pwfTest1, prAverage, s, qlTest2, pwfTest2);
//        }

        double qMax = qlTest1 / (1.8 * feTest * (1- (pwfTest1 / prAverage)) - 0.8*(Math.pow(1- (pwfTest1 / prAverage), 2) * Math.pow(feTest, 2)));

        double qMaxActual = qMax * (0.624 + 0.376 * (fe));

        return qMaxActual;

    }



    public static double getFe(double qlTest1, double pwfTest1, double prAverage, double s,
                                double qlTest2, double pwfTest2) {

        double fe;
        if (s == -1) {
            fe = ( 2.25*((1-(pwfTest1/prAverage)*qlTest2) - (1-(pwfTest2/prAverage)*qlTest1)) ) / ( (Math.pow(1-pwfTest1/prAverage, 2)*qlTest2) - (Math.pow(1-(pwfTest2/prAverage), 2)*qlTest1) );
        } else {
            fe = 7 / (7+s);
        }

        return fe;

    }


}
