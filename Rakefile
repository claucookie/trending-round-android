require "yaml"
require "openssl"
require "tempfile"
require "json"
require "net/http"
require "rexml/document"
require "rubygems"

# A class representing the build process of continuous integration for Android projects.
class Build
  public
  # Initializes the internal structures needed to compile, sign, package and publish
  # the app being built.
  # Params:
  # +receiver+:: the receiver of the build. Related to certificates and provisioning profiles.
  # +build_type+:: the type of build. This can be
  # - snapshot: takes a picture of the status of the implementation by only running unit and component tests.
  # - nightly: runs all tests, compiles the main target and publishes the installer.
  # - release: compiles the main target for internal release and client demo and publishes both installers.
  def initialize(receiver, build_type)
    @receiver = receiver
    @build_type = build_type
    # Load build parameters.
    @params = YAML.load_file("build.yml").merge(YAML.load_file("certs/#{@receiver}/config.yml"))

    @android_sdk = ENV["ANDROID_HOME"]

    @keystore = @params["certificate"]["key_store"]
    @keystore_file = "#{Dir.pwd}/certs/#{@receiver}/#{@keystore}"
    @keystore_pwd = @params["certificate"]["key_store_password"]
    @alias = @params["certificate"]["alias"]
    @alias_pwd =  @params["certificate"]["alias_password"]
    @directory = @params["project_file_name"]
  end
  
  def start(publish = true)
    Dir.chdir(@directory) do
      set_release_version_and_build_number if @build_type != "snapshot"
    end
    
    compile_and_test if @build_type == "snapshot"
    package unless @build_type == "snapshot"
      
    Dir.chdir(@directory) do
      cleanup_after_set_release_version_and_build_number if @build_type != "snapshot"    
      publish_on_hockeyapp unless publish == false
      publish_on_testflight unless publish == false  
    end    
  end
  
  private

  def iconify_release_version?
    should_version = true
    should_version = @params["builds"][@build_type]["icon_versions"] unless @params["builds"][@build_type]["icon_versions"].nil?
    should_version
  end
  
  def set_release_version_and_build_number    
    git_version = `git log -1 --pretty=oneline`[0,7]
    if iconify_release_version?
      sizes = ["mdpi", "hdpi", "xhdpi", "xxhdpi"]
      # image generation (base icon must be named ic_launcher, and the icon in the manifest must be icon)
      # `brew install ghostscript` # we should use this line on mac os x
      sizes.each do |size|
        dir_name = "src/main/res/drawable-#{size}/"
        if Dir.exist?(dir_name)
          Dir.chdir(dir_name) do
            base_file = "ic_launcher.png"      
            target_file = "icon.png"
            if @build_type == "nightly" || @build_type == "release"
              width = `find . -name #{base_file} -type f | xargs -n1 identify -format %w`.strip
              new_size = "#{width}x40"
              `find . -name #{base_file} -type f | xargs -l -i convert -background '#0008' -fill white -gravity center -size #{new_size} -font Corsiva caption:"#{git_version} #{@build_type}" {} +swap -gravity south -composite #{target_file}`
            else
              FileUtils.cp(base_file,target_file)
            end
          end
        end
      end
    end

    # hack into AndroidManifest so we can edit version codes
    manifest_path = "#{Dir.pwd}/src/main/AndroidManifest.xml"
    FileUtils.cp(manifest_path, "#{manifest_path}.bak")
    version = @build_type == "release" ? ENV["VERSION"] : @params["builds"][@build_type]["default_version"] + " " + git_version
    commit_count = `git rev-list HEAD --count`.strip
    file = File.new(manifest_path)
    doc = REXML::Document.new(file)
    doc.root.attributes['android:versionName'] = version
    doc.root.attributes['android:versionCode'] = commit_count
    File.open(file, 'w') do |data|
      data << doc
    end
  end

  def compile_and_test
    # Compile in gradle 
    run("bash gradlew assembleDebug -PkeyStore=\"#{@keystore_file}\" -PkeyStorePassword=\"#{@keystore_pwd}\" -PkeyAlias=\"#{@alias}\" -PkeyAliasPassword=\"#{@alias_pwd}\"")
  end
  
  def package
    build_path = get_build_path
    run("bash gradlew build -PkeyStore=\"#{@keystore_file}\" -PkeyStorePassword=\"#{@keystore_pwd}\" -PkeyAlias=\"#{@alias}\" -PkeyAliasPassword=\"#{@alias_pwd}\"")
  end
  
  def cleanup_after_set_release_version_and_build_number
    # Rollback to all icons
    if iconify_release_version?
      sizes = ["mdpi", "hdpi", "xhdpi", "xxhdpi"]
      # image generation (base icon must be named ic_launcher, and the icon in the manifest must be icon)
      sizes.each do |size|
        dir_name = "src/main/res/drawable-#{size}/"
        if Dir.exist?(dir_name)
          Dir.chdir(dir_name) do
            base_file = "ic_launcher.png"      
            target_file = "icon.png"
            FileUtils.cp(base_file,target_file)
          end
        end
      end
    end
    # Rollback to original AndroidManifest
    manifest_path = "#{Dir.pwd}/src/main/AndroidManifest.xml"
    FileUtils.cp("#{manifest_path}.bak", manifest_path)
    FileUtils.rm("#{manifest_path}.bak")
  end
  
  def publish_on_testflight
     return if @params["builds"][@build_type]["testflight"] == nil
      
     generated_file_name = @params["builds"][@build_type]["main_target"]
     build_path = "#{get_build_path}/#{generated_file_name}"

     testflight_api_token = @params["builds"][@build_type]["testflight"]["api_token"]
     testflight_team_token = @params["builds"][@build_type]["testflight"]["team_token"][@receiver]  
     testflight_notes = @build_type == "release" ? "" : get_build_notes
     testflight_distribution_lists = @params["builds"][@build_type]["testflight"]["distribution_lists"][@receiver].join(", ")
     run("curl --silent http://testflightapp.com/api/builds.json -F file=@#{build_path} -F api_token='#{testflight_api_token}' -F team_token='#{testflight_team_token}' -F notes='#{testflight_notes}' -F notify=False -F replace=True -F distribution_lists='#{testflight_distribution_lists}'")
   end
  
  def publish_on_hockeyapp
    return if @params["builds"][@build_type]["hockeyapp"] == nil 
    
    generated_file_name = @params["builds"][@build_type]["main_target"]
    build_path = "#{get_build_path}/#{generated_file_name}"

    # Publish the app in HockeyApp
    hockeyapp_app_id = @params["builds"][@build_type]["hockeyapp"]["app_id"]
    hockeyapp_api_token = @params["builds"][@build_type]["hockeyapp"]["api_token"]
    hockeyapp_notes = @build_type == "release" ? "" : get_build_notes
    hockeyapp_distribution_lists = @params["builds"][@build_type]["hockeyapp"]["distribution_lists"][@receiver].join(",")
    run("curl -F \"status=2\" -F \"notify=0\" -F \"notes=#{hockeyapp_notes}\" -F \"notes_type=0\" -F \"ipa=@#{build_path}\" -F \"tags=#{hockeyapp_distribution_lists}\" -H \"X-HockeyAppToken: #{hockeyapp_api_token}\" https://rink.hockeyapp.net/api/2/apps/#{hockeyapp_app_id}/app_versions")
  end
  
  def get_build_path
    "#{Dir.pwd}/build/apk"
  end
  
  def get_build_notes
    `git log -10 --pretty=format:"%h - %an, %ar : %s"`
  end
  
  def run(command)
    print "#{command}... "
    output = `#{command} 2> last_error.log`
    if $?.exitstatus > 0 then
      print "Failed!\n"
      puts output
      exit(-1)
    else
      print "Done!\n"
    end
  end
end

class BuildConstructor
  def initialize(build_type)
    @build_type = build_type
    @params = YAML.load_file("build.yml")
    @build = @params["builds"][build_type]
  end
  def start
    if @build.nil? || @build_type == "snapshot"
      # hardcoded value because it won't be needed
      snapshot = Build.new("debug", "snapshot")
      snapshot.start(false)
    else
      distribution_lists = @build["hockeyapp"]["distribution_lists"]
      distribution_lists.each do |cert, tags|
        build = Build.new(cert, @build_type)
        build.start
      end
    end
  end
end

namespace :build do
  desc 'Generate a snapshot version: check if it compiles and if it passes the unit tests'
  task :snapshot do
    build = BuildConstructor.new("snapshot")
    build.start
  end
  
  desc 'Generate a nightly version: compile a signed bleeding edge version automatically at night'
  task :nightly do
    build = BuildConstructor.new("nightly")
    build.start
  end
  
  desc 'Generate a release version: compile a signed version on demand'
  task :release do
    build = BuildConstructor.new("release")
    build.start
  end
end

namespace :config do
  desc 'Debug key copying to local machine'
  task :debug_key do
    puts 'Storing a backup of your local debug key...'.cyan

    new_debug_keystore_path = "~/#{Time.now.to_i}_debug.keystore"
    FileUtils.cp(File.expand_path('~/.android/debug.keystore'), File.expand_path(new_debug_keystore_path))
    puts 'Stored in '+new_debug_keystore_path
    
    puts 'Copying common debug key to the local machine...'.cyan
    
    FileUtils.cp(File.expand_path("./certs/debug/debug.keystore"), File.expand_path('~/.android/debug.keystore'))
    
    puts 'Done!'.green
  end
end

namespace :generate do
  desc 'Web services consumer code generator'
  task :ws do
    puts 'Cleaning previous executions (if any)...'.cyan
    `rm -rf ws-gen >/dev/null`
    `rm -rf ws-def >/dev/null`

    puts 'Downloading definitions and generator'.cyan
    `git clone https://github.com/mobivery/service-generator.git ws-gen`
    `git clone https://github.com/mobivery/service-definitions.git ws-def`

    puts 'Loading generator parameters'.cyan
    @params = YAML.load_file("generate.yml")
    project_file_name = @params["project_file_name"]
    ws_definitions_file_name = @params["ws_definitions_file_name"]
    spreadsheet_name = @params["spreadsheet_name"]
    package = @params["package"]

    base_dir = Dir.pwd
    definition_path = base_dir + "/ws-def/#{ws_definitions_file_name}.xml"
    generated_code_path = base_dir + "/#{project_file_name}/src/main/java/"

    puts 'Generating code'.cyan
    Dir.chdir 'ws-gen' do
      `bundle install`
      `ruby generator.rb -f #{definition_path} -package #{package} -pn #{project_file_name} -aOutput #{generated_code_path} -aVersion 3.0`
    end

    puts 'Deleting temporals'.cyan
    `rm -rf ws-def > /dev/null`
    `rm -rf ws-gen > /dev/null`

    puts 'Done!'.green

  end

  desc 'Localizable generator'
  task :loc do
    puts 'Downloading or updating localio gem'.cyan
    `LANG=en_US.UTF-8 gem install localio`

    puts 'Generating localizables'.cyan
	  `localize`

    puts 'Done!'.green
  end

  desc 'Generate everything!'
  task :all do
    Rake::Task["generate:loc"].invoke
    Rake::Task["generate:ws"].invoke
  end

  # Run setup by default
  task :default => :all
end

class String
  def self.colorize(text, color_code)
    "\e[#{color_code}m#{text}\e[0m"
  end

  def cyan
    self.class.colorize(self, 36)
  end

  def green
    self.class.colorize(self, 32)
  end
end
