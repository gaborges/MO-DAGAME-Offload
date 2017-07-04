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

function [A12, A12_overall] = A12experiment(experiment_path)

fid = fopen([strcat(experiment_path,'/specifications')]);
if fid
fgetl(fid); % Experiment name

line = fgetl(fid); % Algorithms
algorithms = strsplit(line, ' ');

line = fgetl(fid); % Problems
problems = strsplit(line, ' ');

line = fgetl(fid); % Quality indicators
indicators = strsplit(line, ' ');

[A12, A12_overall] = A12stats(experiment_path, algorithms, problems, indicators);

% Save A12 to CSV file
csvwrite(strcat(experiment_path, '/a12.csv'), A12);
csvwrite(strcat(experiment_path, '/a12_overall.csv'), A12_overall);
end
