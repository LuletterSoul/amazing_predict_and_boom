package com.elasticcloudservice.predict;

import java.util.Random;

public class LinearRegression
{
    private int[][] trainData; //训练数据
    private int dayForPredict;
    private int flavorNum;  //flavor的数目
    private double[][][] theta;     //要训练的参数
    private double[] theta0;
    private double alpha;//步长
    private int iteration;//迭代次数
    private boolean mark;

    public LinearRegression(int[][] trainData,int flavorNum,int dayForPredict,double alpha,int iteration)
    {
        this.trainData = trainData;
        this.dayForPredict = dayForPredict; //
        this.alpha = alpha;//参考步长为0.001参考数据10天
        this.iteration=iteration;//参考迭代次数为 100000
        this.flavorNum = flavorNum; //初赛15个
        theta0 = new double[flavorNum];
        theta = new double [flavorNum][dayForPredict][flavorNum];
        initialize_theta();
    }

    private void initialize_theta()//将theta各个参数全部初始化为0.0
    {
        Random random = new Random();
        for(int i=0;i<theta0.length;i++){
            theta0[i] = 0.0+random.nextDouble();
        }
        for(int i=0;i<theta.length;i++){
            for(int j=0;j<theta[i].length;j++){
                for(int k = 0 ;k<theta[i][j].length;k++){
                    theta[i][j][k] = 0.0+random.nextDouble();
                }
            }
        }
    }


    private double[] compute_partial_detivative_for_theta0(){
        double[] result = new double[flavorNum];
        for(int n = 0 ; n<flavorNum;n++){
            result[n] = 0.0;
        }
        for(int n = 0;n<flavorNum;n++){
            for(int k = dayForPredict;k<trainData.length;k++){
                result[n] += theta0[n];
                int r = 0;
                for(int p = k-dayForPredict;p<k;p++,r++){
                    for(int q = 0;q<flavorNum;q++){
                        result[n] += theta[n][r][q]*trainData[p][q];
                    }
                }
                result[n] -= trainData[k][n];
            }
            result[n]/=(trainData.length-dayForPredict);
        }
        return result;
    }

    private double  compute_partial_derivative_for_theta(int n,int i,int j)
    {
        double sum = 0.0;
        double result ;
        for(int k = dayForPredict;k<trainData.length;k++){
            result = theta0[n];
            int r = 0;
            for(int p = k-dayForPredict;p<k;p++,r++){
                for(int q = 0;q<flavorNum;q++){
                    result += theta[n][r][q]*trainData[p][q];
                }
            }
            result -= trainData[k][n];
            result *= trainData[k-dayForPredict+i][j];
            sum+=result;
        }
        return sum/(trainData.length-dayForPredict);
    }

    private double[][][] compute_partial_derivative(){
        double[][][] partial_derivative = new double[flavorNum][dayForPredict][flavorNum];
        for(int n = 0;n<flavorNum;n++){
            for(int i=0;i<dayForPredict;i++){
                for(int j=0;j<flavorNum;j++){
                    partial_derivative[n][i][j] = compute_partial_derivative_for_theta(n,i,j);
                }
                //较之前修改过
            }
        }
        return partial_derivative;
    }

    public void trainTheta()
    {
        int iteration = this.iteration;
        while( (iteration--)>0 )
        {
            //对每个theta i 求 偏导数
            double[] partial_derivative_for_theta0 = compute_partial_detivative_for_theta0();

            for(int n =0;n<flavorNum;n++){
                if(partial_derivative_for_theta0[n]<1e-5){
                    mark = true;
                }else{
                    mark = false;
                }
                theta0[n]-=alpha*partial_derivative_for_theta0[n];
//                if(theta0[n]<0){
//                    theta0[n] = 0.001;
//                }
            }

            double [][][] partial_derivative = compute_partial_derivative();//偏导数
            //更新每个theta
            for(int n =0; n< flavorNum;n++){
                for(int i = 0 ;i<dayForPredict;i++){
                    for(int j = 0;j<flavorNum;j++){
                        if(partial_derivative[n][i][j]<1e-5){
                            mark = true;
                        }else{
                            mark = false;
                        }
                        theta[n][i][j] -= alpha*partial_derivative[n][i][j];
//                        if(theta[n][i][j]<0){
//                            theta[n][i][j] = 0.001;
//                        }
                    }
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
                    for(int q = 0;q<flavorNum;q++){
                        nextData[i][n] += theta[n][k][q]*trainData[p][q];
                    }
                    k++;
                }
                int t = 0;
                if(i>dayForPredict){
                    t = i-dayForPredict;
                }
                while(k<dayForPredict){
                    for(int q = 0;q<flavorNum;q++){
                        nextData[i][n] += theta[n][k][q]*nextData[t++][q];
                    }
                    k++;
                }
                System.out.print(nextData[i][n]+"  ");
            }
            System.out.println();
        }
        return nextData;
    }
}
