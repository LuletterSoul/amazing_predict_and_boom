package com.elasticcloudservice.predict;

public class LinearRegression
{
    private int[][] trainData; //训练数据
    private int dayForPredict;
    private int flavorNum;  //flavor的数目
    private double[][][] theta;     //要训练的参数
    private double[] theta0;
    private double alpha;//步长
    private int iteration;//迭代次数

    public LinearRegression(int[][] trainData,int flavorNum,int dayForPredict,double alpha,int iteration)
    {
        this.trainData = trainData;
        this.dayForPredict = dayForPredict; //参考数据10天
        this.alpha = alpha;//参考步长为0.001
        this.iteration=iteration;//参考迭代次数为 100000
        this.flavorNum = flavorNum; //初赛15个
        theta0 = new double[flavorNum];
        theta = new double [flavorNum][dayForPredict][flavorNum];
        initialize_theta();
    }

    private void initialize_theta()//将theta各个参数全部初始化为1.0
    {
        for(int i=0;i<theta0.length;i++){
            theta0[i] = 1.0;
        }
        for(int i=0;i<theta.length;i++){
            for(int j=0;j<theta[i].length;j++){
                for(int k = 0 ;k<theta[i][j].length;k++){
                    theta[i][j][k] = 1.0;
                }
            }
        }
    }


    private double[] compute_partial_detivative_for_theta0(){
        double[] result = new double[flavorNum];
        for(int n =0;n<flavorNum;n++){
            result[n] = theta0[n];
        }
        for(int n = 0;n<flavorNum;n++){
            for(int k = dayForPredict;k<trainData.length;k++){
                for(int p = k-dayForPredict;p<k;p++){
                    for(int q = 0;q<flavorNum;q++){
                        result[n] += theta[n][p][q]*trainData[p][q];
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
        double result = 0.0;
        for(int k = dayForPredict;k<trainData.length;k++){
            result = theta0[n];
            for(int p = k-dayForPredict;p<k;p++){
                for(int q = 0;q<flavorNum;q++){
                    result += theta[n][p][q]*trainData[p][q];
                }
            }
            result -= trainData[k][n];
            result *= trainData[k-dayForPredict+i][j];
        }
        return result/(trainData.length-dayForPredict);
    }

    private double[][][] compute_partial_derivative(){
        double[][][] partial_derivative = new double[flavorNum][dayForPredict][flavorNum];
        for(int n = 0;n<flavorNum;n++){
            for(int i=0;i<dayForPredict;i++){
                for(int j=0;j<dayForPredict;j++){
                    partial_derivative[n][i][j] = compute_partial_derivative_for_theta(n,i,j);
                }

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
                theta0[n]-=alpha*partial_derivative_for_theta0[n];
            }

            double [][][] partial_derivative = compute_partial_derivative();//偏导数
            //更新每个theta
            for(int n =0; n< flavorNum;n++){
                for(int i = 0 ;i<dayForPredict;i++){
                    for(int j = 0;j<flavorNum;j++){
                        theta[n][i][j] -= alpha*partial_derivative[n][i][j];
                    }
                }
            }
        }
    }

    public double[][] predict(int nextDay){
        double[][] nextData = new double[nextDay][flavorNum];
        for(int i = 0; i < nextDay; i++){
            for(int n=0;n<flavorNum;n++){
                nextData[i][n] = theta0[n];
                int k = 0;
                for(int p = trainData.length - dayForPredict + i;p<trainData.length;p++){
                    for(int q = 0;q<flavorNum;q++){
                        nextData[i][n] += theta[n][k++][q]*trainData[p][q];
                    }
                }
                int t = 0;
                while(k<dayForPredict){
                    for(int q = 0;q<flavorNum;q++){
                        nextData[i][n] += theta[n][k++][q]*nextData[t][q];
                    }
                }
                System.out.print(nextData[i][n]+"  ");
            }
            System.out.println();
        }
        return nextData;
    }
}
