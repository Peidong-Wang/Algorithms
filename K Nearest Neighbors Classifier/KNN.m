dimension_p = linspace(1, 101, 11);

% 1-NN Classifier
result_1NN = linspace(1, 11, 11);
for i = 1:11
    gauss1_mu = zeros(1, dimension_p(i));
    gauss2_mu = zeros(1, dimension_p(i));
    gauss2_mu(1) = 3;
    gauss_sigma = eye(dimension_p(i));
    dividing_point = binornd(200, 0.5);
    gauss1_points = mvnrnd(gauss1_mu, gauss_sigma, dividing_point);
    gauss2_points = mvnrnd(gauss2_mu, gauss_sigma, 200 - dividing_point);
    train_points = [gauss1_points; gauss2_points];
    test_dividing_point = binornd(1000, 0.5);
    test_gauss1_points = mvnrnd(gauss1_mu, gauss_sigma, test_dividing_point);
    test_gauss2_points = mvnrnd(gauss2_mu, gauss_sigma, 1000 - test_dividing_point);
    test_points = [test_gauss1_points; test_gauss2_points];
    [test_min_num, test_result] = min(repmat(sum(train_points.^2, 2), 1, 1000) - 2.*train_points*(test_points)' + (repmat(sum(test_points.^2, 2), 1, 200))');
    test_result = test_result>dividing_point; % 0 denotes the first gaussian, 1 denotes the second
    test_label = [zeros(1, test_dividing_point), ~zeros(1, 1000 - test_dividing_point)]; % 0 denotes the first gaussian, 1 denotes the second
    result_1NN(i) = sum(mod(test_result + test_label, 2))/1000.0;
end

figure(1);
plot(dimension_p,result_1NN,'-*');
title('Error rate versus Dimension p for 1-NN');
xlabel('Dimension p');
ylabel('Error rate');

%3-NN Classifier
result_3NN = linspace(1, 11, 11);
for i = 1:11
    gauss1_mu = zeros(1, dimension_p(i));
    gauss2_mu = zeros(1, dimension_p(i));
    gauss2_mu(1) = 3;
    gauss_sigma = eye(dimension_p(i));
    dividing_point = binornd(200, 0.5);
    gauss1_points = mvnrnd(gauss1_mu, gauss_sigma, dividing_point);
    gauss2_points = mvnrnd(gauss2_mu, gauss_sigma, 200 - dividing_point);
    train_points = [gauss1_points; gauss2_points];
    test_dividing_point = binornd(1000, 0.5);
    test_gauss1_points = mvnrnd(gauss1_mu, gauss_sigma, test_dividing_point);
    test_gauss2_points = mvnrnd(gauss2_mu, gauss_sigma, 1000 - test_dividing_point);
    test_points = [test_gauss1_points; test_gauss2_points];
    [test_min_num, test_sort] = sort(repmat(sum(train_points.^2, 2), 1, 1000) - 2.*train_points*(test_points)' + (repmat(sum(test_points.^2, 2), 1, 200))');
    test_sort = test_sort>dividing_point; % 0 denotes the first gaussian, 1 denotes the second
    test_result = sum(test_sort(1:3,:))>1;
    test_label = [zeros(1, test_dividing_point), ~zeros(1, 1000 - test_dividing_point)]; % 0 denotes the first gaussian, 1 denotes the second
    result_3NN(i) = sum(mod(test_result + test_label, 2))/1000.0;
end

figure(2);
plot(dimension_p,result_3NN,'-*');
title('Error rate versus Dimension p for 3-NN');
xlabel('Dimension p');
ylabel('Error rate');

% Comparison between 1-NN and 3-NN
figure(3);
plot(dimension_p,result_1NN,'b-*', dimension_p, result_3NN, 'r--+');
legend('1-NN','3-NN');
title('Error rate versus Dimension p for both 1-NN and 3-NN');
xlabel('Dimension p');
ylabel('Error rate');
