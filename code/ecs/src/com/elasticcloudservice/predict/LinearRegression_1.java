package com.elasticcloudservice.predict;

import java.util.Random;

//之和自己的过去有关系
public class LinearRegression_1 {

    private int[][] trainData; //训练数据
    private int dayForPredict;
    private int flavorNum;  //flavor的数目
    private double[][] theta;     //要训练的参数
    private double[] theta0;
    private double alpha;//步长
    private int iteration;//迭代次数
    private boolean mark;

    public LinearRegression_1(int[][] trainData, int flavorNum, int dayForPredict, double alpha, int iteration) {
        System.out.println("flavorNum is "+flavorNum+"\r\n"
        +"dayForPredict is "+dayForPredict+"\r\n"
        +"alpha is "+alpha+"\r\n"
        +"iteration is "+iteration);
        this.trainData = trainData;
        this.dayForPredict = dayForPredict;
        this.alpha = alpha;
        this.iteration = iteration;
        this.flavorNum = flavorNum;
        this.mark = false;
        theta0 = new double[flavorNum];
        theta = new double[flavorNum][dayForPredict];
        initialize_theta();
    }

    //随机初始化参数
    private void initialize_theta()
    {
        Random random = new Random();
        for (int i = 0; i < theta0.length; i++) {
            theta0[i] = 0.0+random.nextDouble();
        }
        for (int i = 0; i < theta.length; i++) {
            for (int j = 0; j < theta[i].length; j++) {
                theta[i][j] = 0.0+random.nextDouble();
            }
        }
    }


    //检查过感觉没问题
    private double[] compute_partial_derivative_for_theta0() {
        double[] result = new double[flavorNum];
        for(int n = 0 ; n<flavorNum;n++){
            result[n] = 0.0;
        }
        for (int n = 0; n < flavorNum; n++) {
            for (int k = dayForPredict; k < trainData.length; k++) {
                result[n] += theta0[n];
                int r = 0;
                for (int p = k - dayForPredict; p < k; p++, r++) {
                    result[n] += theta[n][r] * trainData[p][n];
                }
                result[n] -= trainData[k][n];
            }
            result[n] /= (trainData.length - dayForPredict);
        }
        return result;
    }

    private double compute_partial_derivative_for_theta(int n, int i) {
        double sum = 0.0;
        double result;
        for (int k = dayForPredict; k < trainData.length; k++) {
            result = theta0[n];
            int r = 0;
            for (int p = k - dayForPredict; p < k; p++, r++) {
                result += theta[n][r] * trainData[p][n];
            }
            result -= trainData[k][n];
            result *= trainData[k - dayForPredict + i][n];
            sum += result;
        }
        return sum / (trainData.length - dayForPredict);
    }

    private double[][] compute_partial_derivative() {
        double[][] partial_derivative = new double[flavorNum][dayForPredict];
        for (int n = 0; n < flavorNum; n++) {
            for (int i = 0; i < dayForPredict; i++) {
                partial_derivative[n][i] = compute_partial_derivative_for_theta(n, i);
            }
        }
        return partial_derivative;
    }

    public void trainTheta() {
        int iteration = this.iteration;
        while ((iteration--) > 0) {
            //对每个theta i 求 偏导数
            double[] partial_derivative_for_theta0 = compute_partial_derivative_for_theta0();

            for (int n = 0; n < flavorNum; n++) {
                if( partial_derivative_for_theta0[n]<1e-5){
                    mark = true;
                }else mark = false;
                theta0[n] -= alpha * partial_derivative_for_theta0[n];
            }

            double[][] partial_derivative = compute_partial_derivative();//偏导数
            //更新每个theta
            for (int n = 0; n < flavorNum; n++) {
                for (int i = 0; i < dayForPredict; i++) {
                    if( partial_derivative[n][i]<1e-5){
                        mark = true;
                    }else mark = false;
                    theta[n][i] -= alpha * partial_derivative[n][i];
                }
            }
            if(mark == true){
                System.out.println("该线性回归已收敛,此时的迭代次数为"+iteration);
                break;
            }
        }
    }


    public double[][] predict(int nextDay){
        double[][] nextData = new double[nextDay][flavorNum];
        for(int i = 0; i < nextDay; i++){
            for(int n = 0;n<flavorNum;n++){
                nextData[i][n] = theta0[n];
                int k = 0;
                for(int p = trainData.length - dayForPredict + i;p<trainData.length;p++){
                    nextData[i][n] += theta[n][k]*trainData[p][n];
                    k++;
                }
                int t = 0;
                if(i>dayForPredict){
                    t = i-dayForPredict;
                }
                while(k<dayForPredict){
                    nextData[i][n] += theta[n][k]*nextData[t++][n];
                    k++;
                }
                System.out.print(nextData[i][n]+"  ");
            }
            System.out.println();
        }
        return nextData;
    }
}
