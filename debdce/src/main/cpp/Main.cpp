#include <string>

#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_sinks.h>
#include <spdlog/sinks/basic_file_sink.h>
#include <stdlib.h>
#include <time.h>

#include "DeadcodeElimination.h"
#include "FileManager.h"
#include "Frontend.h"
#include "IntegrationManager.h"
#include "LocalReduction.h"
#include "OptionManager.h"
#include "Profiler.h"
#include "Reduction.h"
#include "Reformat.h"
#include "Report.h"
#include "StatsManager.h"

void initialize() {
  FileManager::Initialize();

  auto ConsolSink = std::make_shared<spdlog::sinks::stdout_sink_mt>();
  auto FileSink = std::make_shared<spdlog::sinks::basic_file_sink_mt>(
      OptionManager::OutputDir + "/log.txt", true);
  //auto Logger = std::make_shared<spdlog::logger>(
  //    "Logger", spdlog::sinks_init_list{ConsolSink, FileSink});
  auto Logger = std::make_shared<spdlog::logger>(
        "Logger", spdlog::sinks_init_list{ConsolSink});
  
  ConsolSink->set_pattern("%v");
  if (OptionManager::Debug) {
    ConsolSink->set_level(spdlog::level::debug);
    FileSink->set_level(spdlog::level::debug);
    Logger->set_level(spdlog::level::debug);
  } else {
    ConsolSink->set_level(spdlog::level::info);
    FileSink->set_level(spdlog::level::info);
    Logger->set_level(spdlog::level::info);
  }
  spdlog::register_logger(Logger);

  IntegrationManager::Initialize();
  Profiler::Initialize();
  //spdlog::get("Logger")->info("Oracle: {}", OptionManager::OracleFile);
  for (auto &File : OptionManager::InputFiles)
    spdlog::get("Logger")->info("Input: {}", File);
  //spdlog::get("Logger")->info("Output Directory: {}", OptionManager::OutputDir);
}

void finalize() {
  IntegrationManager::Finalize();
  FileManager::Finalize();
  Profiler::Finalize();
}

int reduceOneFile(std::string &File) {
  //spdlog::get("Logger")->info("Reduce File: {}", File);
  OptionManager::InputFile = File;

  StatsManager::ComputeStats(OptionManager::InputFile);
  int wc0 = std::numeric_limits<int>::max();
  int wc = StatsManager::GetNumOfWords();

  int Iteration = 0;
  while (wc < wc0) {
    Iteration++;
    //=============
    spdlog::get("Logger")->info("Iteration {} (Word: {})", Iteration, wc);
    //=============    
    wc0 = wc;

    //Remove dangling stmts (e.g., if statement with empty bodies)
    Reduction *LR = new LocalReduction();
    Frontend::Parse(OptionManager::InputFile, LR);
    
    //Further remove unused vars, labels, empty or redundant blocks
    DeadCodeElimination::Run();

    StatsManager::ComputeStats(OptionManager::InputFile);
    wc = StatsManager::GetNumOfWords();
  }

  return wc;
}

int main(int argc, char *argv[]) {
  srand(time(0)); //Set random seed as time
  
  OptionManager::handleOptions(argc, argv);
  initialize();

  Profiler::GetInstance()->beginChisel();

  StatsManager::ComputeStats(OptionManager::InputFiles);
  int wc0 = std::numeric_limits<int>::max();
  int wc = StatsManager::GetNumOfWords();

  if (OptionManager::Stat) {
    StatsManager::Print();
    return 0;
  }

  while (wc < wc0) {
    wc0 = wc;
    wc = 0;
    for (auto &File : OptionManager::InputFiles) //Reduce each file
      wc += reduceOneFile(File);
  }

  Profiler::GetInstance()->endChisel();

  //Report::print();
  finalize();

  return 0;
}
