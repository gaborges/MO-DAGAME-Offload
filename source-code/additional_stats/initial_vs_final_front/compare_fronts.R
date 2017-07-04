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
## compare_fronts.R
## Comparison of the objectives values between the first population and the
## final front

# Check input args
args <- commandArgs(trailingOnly = TRUE)
if (length(args) < 3) {
    stop('Wrong syntax.\nUsage: compare_fronts base_folder experiment_file output_file')
}

# Imports
require("XML")

# Constants
PROBLEMS_PER_TABLE <- 3
FILE_INITIAL_FRONT <- "FUN_INITIAL"
FILE_FINAL_FRONT <- "FUN"

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

# Get objectives names
objectives <- getNodeSet(data, "//*[name()='objective']", fun=xmlToList)
objectives_frame <- data.frame(do.call(rbind, objectives))
objectives_name <- as.character(objectives_frame[,1])

# Writes the table header for the current problems
latex_table_header <- function(problems, objectives, current_table_number, last_table_number) {
    cat("\\begin{table*}[!ht]", fill=TRUE)
    cat(sprintf("\\caption{Comparison of the initial population with the final front. Median and IQR (%d/%d)}", current_table_number, last_table_number), fill = TRUE)
    cat(sprintf("\\label{tab:comparison_initial_vs_final_%d}", current_table_number), fill=TRUE)
    cat("\\centering", fill=TRUE)
    cat("\\begin{scriptsize}", fill=TRUE)
    cat("\\renewcommand*{\\arraystretch}{1.5}", fill=TRUE)
    cat("\\setlength{\\tabcolsep}{.4em}", fill=TRUE)
    cat("\\begin{tabular}{@{} l | l ")
    for (i in (1 : length(problems))) { cat("| c c c ") }
    cat("@{}}", fill=TRUE)
    cat(sprintf("\\multicolumn{%d}{c}{} \\\\", 2 + length(objectives) * length(problems)), fill=TRUE)
    cat("\\toprule", fill=TRUE)
    cat("\\multicolumn{2}{c}{} &", fill=TRUE)
    for (i in (1 : length(problems))) {
        cat(sprintf("\\multicolumn{%d}{c}{%s} ", length(objectives), problems[i]))
        if (i < length(problems)) {
            cat("&")
        } else {
            cat("\\\\", fill=TRUE)
        }
    }
    cat("\\multicolumn{2}{c}{} &", fill=TRUE)
    for (i in (1 : length(problems))) {
        for (j in 1 : length(objectives)) {
            cat(objectives[j])
            if (j < length(objectives) || i < length(problems)) {
                cat(" & ")
            } else {
                cat(" \\\\", fill=TRUE)
            }
        }
    }
    cat("\\midrule", fill=TRUE)
}

get_pretty_null_front <- function() {
    num_rows <- length(objectives_name)
    null_front <- matrix(nrow=num_rows, ncol=2)
    null_front[] <- "-"
}

latex_table_row <- function(algorithm, problems, list_initial_front, list_final_front, list_difference, bottom) {
    cat(sprintf("%% %s", algorithm), fill=TRUE)
    cat(sprintf("\\parbox[t]{2mm}{\\multirow{3}{*}{\\rotatebox[origin=c]{90}{%s}}} &", algorithm))
    cat("Initial & ")
    for (i in (1: length(problems))) {
        initial_front <- list_initial_front[[i]]
        if (is.null(initial_front)) {
            initial_front <- get_pretty_null_front()
        }
        if (!is.null(initial_front)) {
            for (j in (1 : nrow(initial_front))) {
                if (is.double(initial_front[j][1])) {
                    cat(sprintf("$%.2f_{%.2f}$", initial_front[j,][1], initial_front[j,][2]))
                } else {
                    cat(initial_front[j,][1])
                }
                
                if (j < nrow(initial_front) || i < length(problems)) {
                    cat(" & ")
                } else {
                    cat("\\\\", fill=TRUE)
                }
            }
        }
    }
    
    cat("& Final & ")
    for (i in (1: length(problems))) {
        final_front <- list_final_front[[i]]
        if (is.null(final_front)) {
            final_front <- get_pretty_null_front()
        }
        for (j in (1 : nrow(final_front))) {
            if (is.double(final_front[j][1])) {
                cat(sprintf("$%.2f_{%.2f}$", final_front[j,][1], final_front[j,][2]))
            } else {
                cat(final_front[j,][1])
            }
            
            if (j < nrow(final_front) || i < length(problems)) {
                cat(" & ")
            } else {
                cat("\\\\", fill=TRUE)
            }
        }
    }
    
    cat("& Diff. & ")
    for (i in (1: length(problems))) {
        difference <- list_difference[[i]]
        if (is.null(difference)) {
            difference <- matrix(nrow=1,ncols=length(objectives_name))
        }
        for (j in (1 : length(difference))) {
            if (is.double(difference[j])) {
                if (difference[j] >= 0) {
                    cat(sprintf("\\textbf{(+%.2f\\%%)}", difference[j]))
                } else {
                    cat(sprintf("\\textbf{(%.2f\\%%)}", difference[j]))
                }
            } else {
                cat(difference[j])
            }
            
            if (j < length(difference) || i < length(problems)) {
                cat(" & ")
            } else {
                cat("\\\\", fill=TRUE)
            }
        }
    }
    
    if (bottom) {
        cat("\\bottomrule", fill=TRUE)
    } else {
        cat("\\midrule", fill=TRUE)
    }
}

latex_table_close <- function() {
    cat("\\end{tabular}", fill=TRUE)
    cat("\\end{scriptsize}", fill=TRUE)
    cat("\\end{table*}", fill=TRUE)
}

# Returns a matrix
# A row for each objective
# Column 1: mean
# Column 2: IQR
evaluate_front <- function(front_file, base_folder, algorithm_path, problem_path, independent_runs) {
    data_folder <- paste(sep="/", base_folder, "data", algorithm_path, problem_path)
    data <- NULL
    result <- NULL
    
    for (i in (0:(independent_runs - 1))) {
        file <- paste(sep="/", data_folder, sprintf("%s.%d", front_file, i))
        if (file.info(file)["size"] > 0) {
            data <- rbind(data, read.table(file))
        }
    }
    
    if (!is.null(data)) {
        result <- matrix(nrow=ncol(data), ncol=2)        
        for (i in (1:ncol(data))) {
            if (sum(data[,i]) < 0) {
                data[,i] = -1 * data[,i]
            }
            result[i,][1] = mean(data[,i])
            result[i,][2] = IQR(data[,i])
        }
    }
    
    return(result)
}

# Returns, for each objective, the difference (in %) between the final and the
# initial front.
front_diff <- function(initial_front, final_front) {
    result <- NULL
    if (!is.null(initial_front)) {
        num_objectives <- nrow(initial_front)
        result <- double(num_objectives)
        for (i in (1 : num_objectives)) {
            result[i] <- (final_front[i][1] - initial_front[i][1]) / initial_front[i][1] * 100;
        }
    }
    
    return(result)
}

next_problem_index <- 1
current_table_index <- 1
last_table_number <- ceiling(length(problems_name) / PROBLEMS_PER_TABLE)
current_table_number <- 1
while (next_problem_index < length(problems_name)) {
    problems_in_this_table <- (next_problem_index : min(next_problem_index + PROBLEMS_PER_TABLE - 1, length(problems_name)))
    latex_table_header(problems_name[problems_in_this_table], objectives_name, current_table_number, last_table_number)
    for (i in (1 : length(algorithms_name))) {
        list_initial_front <- list()
        list_final_front <- list()
        list_difference <- list()
        for (j in problems_in_this_table) {
            index <- j - next_problem_index + 1
            list_initial_front[[index]] <- evaluate_front(FILE_INITIAL_FRONT, base_folder, algorithms_path[i], problems_path[j], independent_runs)
            list_final_front[[index]] <- evaluate_front(FILE_FINAL_FRONT, base_folder, algorithms_path[i], problems_path[j], independent_runs)
            list_difference[[index]] <- front_diff(list_initial_front[[index]], list_final_front[[index]])
        }
        latex_table_row(algorithms_name[i], problems[problems_in_this_table], list_initial_front, list_final_front, list_difference, i == length(algorithms_name))
    }
    
    next_problem_index <- next_problem_index + PROBLEMS_PER_TABLE   
    current_table_number <- current_table_number + 1
    latex_table_close()
}


