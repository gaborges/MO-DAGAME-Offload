##
## Copyright 2014 Gustavo García Pascual, Mónica Pinto and Lidia Fuentes
##
## This file is part of MO-DAGAME
##
## MO-DAGAME is free software: you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation, either version 3 of the License, or
## (at your option) any later version.
##
## MO-DAGAME is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with MO-DAGAME.  If not, see <http://www.gnu.org/licenses/>.
########################################################################
## compare_fix_time.R
## Calculates the proportion of the execution time that is elapsed runnig the
## fix operator.

# Check input args
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 3) {
    stop('Wrong syntax.\nUsage: compare_fix_time base_folder experiment_file output_file')
}

# Imports
require("XML")

# Constants
FILE_TIME <- "TIME"
FILE_FIX_TIME <- "FIXTIME"

base_folder <- args[1]
specs_file_name <- args[2]
specs_file <- paste(sep="/", base_folder, specs_file_name)
output_file <- args[3]
sink(output_file)

# Load data from XML
data <- xmlParse(specs_file)

# Get number of independent runs
study <- getNodeSet(data, "//*[name()='study']")
independent_runs <- as.integer(sapply(study, function(node) xmlGetAttr(node, "independent_runs")))

# Get algorithms names and folders
algorithms <- getNodeSet(data, "//*[name()='algorithm']", fun=xmlToList)
algorithms_frame <- data.frame(do.call(rbind, algorithms))
algorithms_name <- as.character(algorithms_frame[,1])
algorithms_path <- as.character(algorithms_frame[,2])

# Get problems names and folders
problems <- getNodeSet(data, "//*[name()='problem']", fun=xmlToList)
problems_frame <- data.frame(do.call(rbind, problems))
problems_name <- as.character(problems_frame[,1])
problems_path <- as.character(problems_frame[,2])

# Calculate the result for an algorithm and problem in particular
process_problem <- function(algorithm_index, problem_index) {
    time_file <- paste(sep="/", base_folder, "data", algorithms_path[algorithm_index], problems_path[problem_index], FILE_TIME)
    time <- scan(time_file)
    
    # Oops, we don't have the FIXTIME file which contains all the individual files
    fixtime_file <- paste(sep="/", base_folder, "data", algorithms_path[algorithm_index], problems_path[problem_index], FILE_FIX_TIME)
    fixtime <- double()
    for (i in (0 : (independent_runs - 1))) {
        fixtime <- rbind(fixtime, scan(paste(sep=".", fixtime_file, i)))
    }
    
    return(mean(fixtime/time))
}

# Main
for (i in (1 : length(algorithms_name))) {
    cat(sprintf("%% %s", algorithms_name[i]), fill=TRUE)
    for (j in (1 : length(problems_name))) {
        result <- process_problem(i, j)
        cat(sprintf("%s %.2f%%", problems_name[j], result*100), fill=TRUE)
    }
    # Blank line
    cat("", fill=TRUE)
}
