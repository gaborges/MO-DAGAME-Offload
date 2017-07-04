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

function [ranks, from]  = ranking(data1, data2)

    % WARNING - We assume that NaN means Inf!
    % This is usually the case in quality indicators where lower values are
    % better
    data1(find(isnan(data1)))=Inf;
    data2(find(isnan(data2)))=Inf;
    
    % Sort data
    data = sort([data1(:)' data2(:)']);
    ranks = 1:length(data);
    
    i = 1;
    while i <= length(data)
        % Find the apparitions of this value in particular
        indexes = find(data == data(i), length(data), 'last');
        
        ranks(indexes(1) : indexes(end)) = mean(indexes(1) : indexes(end));
        
        hist1 = histc(data1, data(i));
        hist2 = histc(data2, data(i));
        
        if hist1 > 0
            from(i : (i + hist1 - 1)) = 1;
        end
        
        if hist2 > 0
            from(i + hist1 : i + hist1 + hist2 - 1) = 2;
        end
        
        i = indexes(end) + 1;
    end
end
