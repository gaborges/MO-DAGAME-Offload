library(ggplot2)
library(dplyr)
library(tidyr)

fileEntries = 100
rootFolder = "C:/Users/Guilherme/Desktop/offload-experiments/experimentosCorretos/"

fileNames <- c("TIME.LATENCY")

# colocar lista de arquivos e concatenar os nomes com os locais (função paste)
cloudfolderNames <- c(
  "SPLOT-3CNF-FM-500-50-1.00-SAT-1")

# colocar lista de arquivos e concatenar os nomes com os locais (função paste)
mainFolderNames <- c(
  "cloud-MotoE-MoDagameNSGAII_OFFLOAD",
  "cloud-MotoE-MODagamePAES_OFFLOAD",
  "cloud-MotoG-MoDagameNSGAII_OFFLOAD",
  "cloud-MotoG-MODagamePAES_OFFLOAD",
  "cloud-S6-MoDagameNSGAII_OFFLOAD",
  "cloud-S6-MoDagamePAES_OFFLOAD"
)
cloudletFolders <- c(
  "remote-server-MotoE-MoDagameNSGAII_OFFLOAD",
  "remote-server-MotoE-MODagamePAES_OFFLOAD",
  "remote-server-MotoG-MoDagameNSGAII_OFFLOAD",
  "remote-server-MotoG-MODagamePAES_OFFLOAD",
  "remote-server-S6-MoDagameNSGAII_OFFLOAD",
  "remote-server-S6-MODagamePAES_OFFLOAD"
)


folderNames <- c("BerkeleyDBMemoryversionProductLine")
# apaga tudo no 0
total <- numeric()
col <- numeric()
group <- character()

for(cloudFolder in mainFolderNames){
  
  for(file in fileNames){
    for(folder in cloudfolderNames){
      #col = rbind(file,col)
      for(i in 0:99){
        rawTime = readLines(paste(rootFolder,"/",cloudFolder,"/",folder,"/",file,".",i,sep=""))
        #vector[i+1] <- as.numeric(rawTime)
        #rbind()
        group = rbind(group,as.numeric(rawTime))
        col = rbind(col,"cloud")
      }
      
    }
  }
}

for(cloudletFolder in cloudletFolders){
  
  for(file in fileNames){
    for(folder in cloudfolderNames){
      #col = rbind(file,col)
      for(i in 0:99){
        rawTime = readLines(paste(rootFolder,"/",cloudletFolder,"/",folder,"/",file,".",i,sep=""))
        #vector[i+1] <- as.numeric(rawTime)
        #rbind()
        group = rbind(group,as.numeric(rawTime))
        col = rbind(col,"remote")
      }
      
    }
  }
}

colnames(group) <- c("TIME")
colnames(col) <- c("ALTERNATIVE")

total = cbind(col,total)
total = cbind(total,group)

rownames(total) <- NULL

write.csv(x=total,file = paste(rootFolder,"timeAllLatency-9.csv",sep=""))

df = read.csv(file = paste(rootFolder,"timeAllLatency-9.csv",sep=""))

dfsum = df %>%
  group_by(ALTERNATIVE) %>%
  summarise(num = n(), mean = mean(TIME), sd = sd(TIME), max=max(TIME), min=min(TIME),
            se = 2*sd/sqrt(num), sem = sd/sqrt(num))

#dfsum=summarise(.data=df,num = n(), mean = mean(TIME), sd = sd(TIME), max=max(TIME), min=min(TIME),
 #               se = 2*sd/sqrt(num), sem = sd/sqrt(num))
#dfsum = df %>%
#  group_by(ALTERNATIVE) %>%
#  summarise(num = n(), mean = mean(TIME), sd = sd(TIME), max=max(TIME), min=min(TIME),
#            se = 2*sd/sqrt(num), sem = sd/sqrt(num))

write.csv(x=dfsum,file = paste(rootFolder,"resultsLatency-9.csv",sep=""))


