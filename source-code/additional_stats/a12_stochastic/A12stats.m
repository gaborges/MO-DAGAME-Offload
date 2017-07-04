% Copyright 2014 Gustavo García Pascual, Mónica Pinto and Lidia Fuentes
%
% This file is part of MO-DAGAME
%
% MO-DAGAME is free software: you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation, either version 3 of the License, or
% (at your option) any later version.
%
% MO-DAGAME is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
%
% You should have received a copy of the GNU General Public License
% along with MO-DAGAME.  If not, see <http://www.gnu.org/licenses/>.

function [A12, A12_overall] = A12stats(experiment_path, algorithms, problems, ...
    qualityIndicators)

basedir  = strcat(experiment_path, '/data/');
nA = length(algorithms);
nP = length(problems);
nQ = length(qualityIndicators);

% Preallocate memory for data
data = cell(nA, nP, nQ);

% Load data. One file for each tuple [algorithm,problem ; qIndicator]
for i = 1 : nA % Algorithms
    for j = 1 : nP % Problems
        for k = 1 : nQ % Quality Indicators
            dataFile = strcat(basedir, algorithms{i}, '/', problems{j}, ...
                '/', qualityIndicators{k});
            data{i}{j}{k} = load(dataFile);
        end
    end
end

% Preallocate memory for A12
A12 = nan((nA - 1) * nP, (nA - 1) * nQ);

% We have to compare, for each problem and the quality indicator, all
% the algorithms
for i = 1 :nP % Problems
    for j = 1 : nQ % Quality indicators
        for k = 1 : nA - 1 % Algorithm 1 list
            data1 = data{k}{i}{j};
            for l = k + 1 : nA % Algorithm 2 list
                row = (i - 1) * (nA - 1) + k;
                col = (j - 1) * (nA - 1) + (l - 1);
                data2 = data{l}{i}{j};
                A12(row, col) = A12calc(data1, data2);
            end
        end
    end
end

% Overall A12 - This matrix gathers the results of all the problems in a
% single list

o_data = cell(nA, nQ);
A12_overall = nan((nA - 1), (nA - 1) * nQ);
problem_length = length(data{1}{1}{1});
for i = 1 : nA
    for j = 1 : nQ
        o_data{i}{j} = zeros(1, nP * problem_length);
    end
end

for i = 1 : nA
    for j = 1 : nP
        for k = 1 : nQ
            vector = o_data{i}{k};
            vector(1 + (j - 1) * problem_length : j * problem_length) = data{i}{j}{k};
            o_data{i}{k} = vector;
        end
    end
end

for i = 1: nQ % Quality indicator
    for j = 1 : nA - 1 % Algorithm 1
        data1 = o_data{j}{i};
        for k = j + 1 : nA % Algorithm 2
            row = j;
            col = (i - 1) * (nA - 1) + (k - 1);
            data2 = o_data{k}{i};
            A12_overall(row, col) = A12calc(data1, data2);
        end
    end
end
        
