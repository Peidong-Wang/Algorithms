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
		br.close();
		int[] result = new int[nodes_num]; // result[0] stores the length. The other nodes_num - 1 elements stores the path, excluding 1 since 1 is always at the beginning and the end.
		result = dp_salesman(nodes_num, adjacency_matrix);
		bw.write("1 ");
		for (i = 1; i < nodes_num - 1; i++){
			bw.write(Integer.toString(result[i] + 1)); // The nodes in result[] are from 0 to nodes_num - 1, so that here we add 1.
			bw.write(" ");
		}
		bw.write(Integer.toString(result[nodes_num - 1] + 1));
		bw.newLine();
		bw.write(Integer.toString(result[0]));
		bw.flush();
		bw.close();
	}

	// dp_salesman() uses bottom up method of dynamic programming. Two main ideas are as follows.
	// First, use stacks for to represent W. Second, use a fixed order to store nodes in W and fetch nodes by calculating indices.
	private static int[] dp_salesman(int nodes_num, int[][] adjacency_matrix){
		int[] results = new int[nodes_num]; // Corresponds to result[] in the main() function.
		int i, j, k, ii, t, v, vv, r, rr, tt = 0; // Auxiliary variables.
		int[][][] length = new int[nodes_num - 1][][]; // Stores the lengths.
		int[][][] path = new int[nodes_num - 1][][]; // Stores the paths of the shortest lengths.
		int W_var_num = 1; // The number of different W variables.
		int[] W_flag = new int[nodes_num]; // Used to denote whether a node is in W. 0 represents that the node is not in W
		Stack<Integer> nodes_stack = new Stack<Integer>(); // Stores current W.
		Stack<Integer> nodes_stack_temp = new Stack<Integer>(); // Used to help operations on nodes_stack.
		int node_temp = 0; // Auxiliary variable used in stack operations.
		int last_num = 0; // An essential variable used for W changing.
		int min_dist = 0; // Denotes distance(v', W - {v'})
		int w_node = 0; // Denotes v'.
		int w_counter = 0; // A counter used to represent indices for length[][][] and path[][][].
		int[][] stack_storage = new int[nodes_num - 1][]; // Denotes the nodes in W. Used to compare with v' for index calculating.
		int stack_storage_prev = 0; // Auxiliary variable used for calculating indices.
		int prev_table_index = 0; // The index of the current W in the previous table. That is, at i - 1.
		int prev_node = 0; // Auxiliary variable used for calculating path[][][].
		int[] result_flag = new int[nodes_num - 1]; // Denotes whether a node has been visited. 0 represents not been visited.
		int result_prev = 0; // Auxiliary variable used for calculating indices in the path finding process.
		for (i = 1; i <= nodes_num - 1; i++){
			W_var_num = C_n_m(nodes_num - 1, i);
			length[i - 1] = new int[nodes_num][W_var_num];
			path[i - 1] = new int[nodes_num][W_var_num];
			last_num = 1;
			w_counter = 0;
			nodes_stack.push(1);
			stack_storage[i - 1] = new int[i];
			while (!(nodes_stack.size() == 0 && last_num == nodes_num - i)){
				ii = last_num + 1;
				while (nodes_stack.size() < i){
					nodes_stack.push(ii);
					ii = ii + 1;
				}
				for (j = 0; j < nodes_num; j++){
					W_flag[j] = 0; // 
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
					if (W_flag[j] == 0){ // Here i --> table index, j --> v, nodes_stack --> W.
						if (i == 1){
							w_node = (Integer) nodes_stack.pop();
							nodes_stack.push(w_node);
							length[i - 1][j][w_counter] = adjacency_matrix[j][w_node] + adjacency_matrix[0][w_node];
							path[i - 1][j][w_counter] = w_node;
						}
						else{
							nodes_stack_temp.clear();
							for (k = 0; k < i; k++){
								w_node = (Integer) nodes_stack.pop(); // Here w_node --> v'.
								nodes_stack_temp.push(w_node);
								prev_table_index = 0;
								vv = 0;
								for (v = 0; v < i; v++){
									if (stack_storage[i - 1][v] != w_node){
										if (vv == 0){
											prev_table_index = prev_table_index + sum_C_n_m(nodes_num - 2, i - 1 - 1 - vv, stack_storage[i - 1][v] - 1);
											stack_storage_prev = stack_storage[i - 1][v];
										}
										else{
											prev_table_index = prev_table_index + sum_C_n_m(nodes_num - 2 - stack_storage_prev, i - 1 - 1 - vv, stack_storage[i - 1][v] - 1 - stack_storage_prev);
											stack_storage_prev = stack_storage[i - 1][v];
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
							for (k = 0; k < i; k++){
								nodes_stack.push((Integer) nodes_stack_temp.pop());
							}
							length[i - 1][j][w_counter] = min_dist;
							path[i - 1][j][w_counter] = prev_node;
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
		return results;
	}
	
	// C_n_m is a function to compute number of permutations which choose m elements from n elements
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

	// sum_C_n_m is a function to compute the interval 
	private static int sum_C_n_m(int collection_space, int ini_collection_num, int loop_num){
		int result = 0;
		int i = 0;
		for (i = 0; i < loop_num; i++){
			result = result + C_n_m(collection_space - i, ini_collection_num);
		}
		return result;
	}

}




















