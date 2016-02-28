//// CSE6331 Traveling Salesman Problem
// Peidong Wang
// Email: wang.7642@osu.edu
// Compiler: javac 1.8.0_66

import java.io.*;
import java.util.Stack;

public class Salesman{
	public static void main(String[] args) throws IOException{
		BufferedReader br = null;
		BufferedWriter bw = null;
		int nodes_num = 0;
		String matrix_row = null;
		int i, j = 0;
		br = new BufferedReader(new FileReader("input.txt"));
		bw = new BufferedWriter(new FileWriter("output.txt"));
		nodes_num = Integer.parseInt(br.readLine());
		int[][] adjacency_matrix = new int[nodes_num][nodes_num];	
		String[] string_elements = new String[nodes_num];
		for (i = 0; i < nodes_num; i++){
			matrix_row = br.readLine();
			string_elements = matrix_row.split(" ");
			for (j = 0; j < nodes_num; j++){
				adjacency_matrix[i][j] = Integer.parseInt(string_elements[j]);
			}
		}
		int[] result = new int[nodes_num];
		result = dp_salesman(nodes_num, adjacency_matrix);
		bw.write("1 ");
		for (i = 1; i < nodes_num - 1; i++){
			bw.write(Integer.toString(result[i] + 1));
			bw.write(" ");
		}
		bw.write(Integer.toString(result[nodes_num - 1] + 1));
		bw.newLine();
		bw.write(Integer.toString(result[0]));
		bw.flush();
	}

	private static int[] dp_salesman(int nodes_num, int[][] adjacency_matrix){
		int[] results = new int[nodes_num]; // The first element in results[] is the length, the followings are the path
		int i, j, k, ii, t, u, v, vv, r, rr, tt = 0;
		int result_temp = 0;
		int[][][] length = new int[nodes_num - 1][][];
		int[][][] path = new int[nodes_num - 1][][];
		int W_var_num = 1; // The number of different W variables.
		int[] W_flag = new int[nodes_num];
		Stack nodes_stack = new Stack();
		Stack nodes_stack_temp = new Stack();
		Stack result_stack = new Stack();
		int node_temp = 0;
		int last_num = 0;
		int min_dist = 0;
		int w_node = 0;
		int w_counter = 0;
		int stack_size = 0;
		int[][] stack_storage = new int[nodes_num - 1][];
		int[][] stack_storage_reduc = new int[nodes_num - 1][];
		int prev_table_index = 0;
		int prev_node = 0;
		int[] result_flag = new int[nodes_num - 1];
		int result_prev = 0;
		int min_prev = 0;
		int w_counter_index = 0;
		int[] w_counter_flag = new int[nodes_num - 1];
		int w_counter_temp = 0;
		for (i = 1; i <= nodes_num - 1; i++){
			W_var_num = C_n_m(nodes_num - 1, i);
			length[i - 1] = new int[nodes_num][W_var_num];
			path[i - 1] = new int[nodes_num][W_var_num];
			last_num = 1;
			w_counter = 0;
			nodes_stack.push(1);
			stack_storage[i - 1] = new int[i];
			stack_storage_reduc[i - 1] = new int[i];
			while (!(nodes_stack.size() == 0 && last_num == nodes_num - i)){
				ii = last_num + 1;
				while (nodes_stack.size() < i){
					nodes_stack.push(ii);
					ii = ii + 1;
				}
				for (j = 0; j < nodes_num; j++){
					W_flag[j] = 0; // 0 denotes that the node is not in W
				}
				for (j = 0; j < i; j++){
					node_temp = (Integer) nodes_stack.pop();
					stack_storage[i - 1][i - 1 - j] = node_temp;
					W_flag[node_temp] = 1;
					nodes_stack_temp.push(node_temp);
				}
				for (j = 0; j < i; j++){
					node_temp = (Integer) nodes_stack_temp.pop();
					nodes_stack.push(node_temp);
				}
				for (j = 0; j < nodes_num; j++){
					if (W_flag[j] == 0){ // Here j denotes v. j <--> v. nodes_stack <--> W.
						if (i == 1){
							w_node = (Integer) nodes_stack.pop();
							nodes_stack.push(w_node);
							length[i - 1][j][w_counter] = adjacency_matrix[j][w_node] + adjacency_matrix[0][w_node];
							path[i - 1][j][w_counter] = w_node;
						}
						else{
							nodes_stack_temp.clear();
							stack_size = nodes_stack.size();
							for (k = 0; k < nodes_num - 1; k++){
								w_counter_flag[k] = 0;
							}
							for (k = 0; k < stack_size; k++){
								w_node = (Integer) nodes_stack.pop();
								nodes_stack_temp.push(w_node);
								w_counter_flag[w_node - 1] = 1;
								for (v = 0; v < i; v++){
									stack_storage_reduc[i - 1][v] = 0;
								}
								prev_table_index = 0;
								vv = 0;
								for (v = 0; v < i; v++){
									if (stack_storage[i - 1][v] != w_node){
										if (vv == 0){
											prev_table_index = prev_table_index + sum_C_n_m(nodes_num - 2, i - 1 - 1 - vv, stack_storage[i - 1][v] - 1);
											stack_storage_reduc[i - 1][vv] = stack_storage[i - 1][v];
										}
										else{
											stack_storage_reduc[i - 1][vv] = stack_storage[i - 1][v];
											prev_table_index = prev_table_index + sum_C_n_m(nodes_num - 2 - stack_storage_reduc[i - 1][vv - 1], i - 1 - 1 - vv, stack_storage_reduc[i - 1][vv] - 1 - stack_storage_reduc[i - 1][vv - 1]);
										}
										vv = vv + 1;
									}
								}
								if (k == 0){
									min_dist = adjacency_matrix[j][w_node] + length[i - 2][w_node][prev_table_index];
									prev_node = w_node;
								}
								else if (min_dist > adjacency_matrix[j][w_node] + length[i - 2][w_node][prev_table_index]){
									min_dist = adjacency_matrix[j][w_node] + length[i - 2][w_node][prev_table_index];
									prev_node = w_node;
								}
							}
							for (k = 0; k < stack_size; k++){
								nodes_stack.push((Integer) nodes_stack_temp.pop());
							}
							length[i - 1][j][w_counter] = min_dist;
							path[i - 1][j][w_counter] = prev_node;
//							w_counter_index = 0;
//							vv = 0;
//							for (v = 0; v < nodes_num - 1; v++){
//								if (w_counter_flag[v] == 1){
//									if (vv == 0){
//										w_counter_index = w_counter_index + sum_C_n_m(nodes_num - 2, (i + 1) - 2 - vv, (v + 1) - 1);
//										w_counter_temp = v + 1;
//									}
//									else{
//										w_counter_index = w_counter_index + sum_C_n_m(nodes_num - 2 - w_counter_temp, (i + 1) - 2 - vv, (v + 1) - 1 - w_counter_temp);
//										w_counter_temp = v + 1;
//									}
//									vv = vv + 1;
//								}
//							}
//							path[i - 1][w_counter_index] = prev_node;
						}
					}
				}
				last_num = (Integer) nodes_stack.pop();
				if (last_num < nodes_num - 1){
					nodes_stack.push(last_num + 1);
				}
				else{
					t = 1;
					while (last_num == nodes_num - t && nodes_stack.size() != 0){
						last_num = (Integer) nodes_stack.pop();
						t = t + 1;
					}
				}
				w_counter = w_counter + 1;
			}
		}
		results[0] = length[nodes_num - 2][0][0];
		rr = 0;
		tt = 0;
		for (r = 0; r < nodes_num - 1; r++){
			results[r + 1] = path[nodes_num - 2 - r][tt][rr];
			result_flag[results[r + 1] - 1] = 1;
			tt = results[r + 1];
			rr = 0;
			vv = 0;
			for (v = 0; v < nodes_num - 1; v++){
				if (result_flag[v] != 1){
					if (vv == 0){
						rr = rr + sum_C_n_m(nodes_num - 2, ((nodes_num - 1) - r) - 1 - 1 - vv, (v + 1) - 1);
						result_prev = v + 1;
					}
					else{
						rr = rr + sum_C_n_m(nodes_num - 2 - result_prev, ((nodes_num - 1) - r) - 1 - 1 - vv, (v + 1) - 1 - result_prev);
						result_prev = v + 1;
					}
					vv = vv + 1;
				}
			}
		}
		for (result_temp = 0; result_temp < nodes_num; result_temp ++){
			if (result_temp == 0){
				System.out.println(Integer.toString(results[result_temp]));
			}
			else{
				System.out.println(Integer.toString(results[result_temp] + 1));
			}
		}
		return results;
	}
	
	private static int C_n_m(int n, int m){
		int result = 1;
		int i = 0;
		for (i = 0; i < n; i++){
			result = result * (n - i);
		}
		for (i = 0; i < m; i++){
			result = result / (m - i);
		}
		for (i = 0; i < n - m; i++){
			result = result / (n - m - i);
		}
		return result;
	}

	private static int sum_C_n_m(int collection_space, int ini_collection_num, int loop_num){
		int result = 0;
		int i = 0;
		for (i = 0; i < loop_num; i++){
			result = result + C_n_m(collection_space - i, ini_collection_num);
		}
		return result;
	}

}




















