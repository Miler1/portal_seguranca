module.exports = function(grunt) {

	require('load-grunt-tasks')(grunt);

	var config = {
		app: 'app',
		tmp: '.tmp',
		dist: '../backend/public',
		pugs: [
			'index.pug',

			'sections/login.pug',
			'sections/inicial.pug',
			'sections/home.pug',
			'sections/usuarios/acesso.pug',
			'sections/modulos/gestao.pug',
			'sections/modulos/cadastroUm.pug',
			'sections/modulos/cadastroDois.pug',
			'sections/modulos/visualizacao.pug',
			'sections/perfis/gestao.pug',
			'sections/perfis/visualizacao.pug',
			'sections/setores/gestao.pug',
			'sections/setores/cadastro.pug',
			'sections/setores/visualizacao.pug'

		]
	};

	grunt.initConfig({

		config: config,

		watch: {

			grunt: {
				files: [
					'Gruntfile.js'
				],
				tasks: ['default']
			},
			less: {
				files: [
					'<%= config.app %>/styles/**/*.less'
				],
				tasks: ['less', 'autoprefixer']
			},
			pug: {
				files: [
					'<%= config.app %>/views/**/*.pug'
				],
				tasks: ['pug:all'],
				options: {
					spawn: false,
				},
			},
			scripts: {
				files: [
					'<%= config.app %>/**/*.js'
				],
				tasks: ['jshint', 'copy:scripts', 'copy:config'],
				options: {
					spawn: false,
				},
			},
			images: {
				files: [
					'<%= config.app %>/images/**/*.{png,jpg,jpeg,gif,webp,svg}'
				],
				tasks: ['copy:images']
			}

		},

		pug: {
			all: {
				options: {
					pretty: true,
					basedir: '<%= config.app %>/views',
				},
				files: [{
					expand: true,
					dest: '<%= config.dist %>/views',
					ext: '.html',
					cwd: '<%= config.app %>/views',
					src: '<%= config.pugs %>'
				}]
			},

			dist: {
				options: {
					pretty: true
				},
				files: {
					'<%= config.dist %>/views/index.html': '<%= config.app %>/views/index.pug.tmp'
				}
			}

		},

		less: {
			dev: {
				files: [
					{
						expand: true,
						compress: true,
						cwd: '<%= config.app %>/styles',
						src: 'main.less',
						dest: '<%= config.tmp %>/styles',
						ext: '.css'
					}
				]
			}
		},

		autoprefixer: {
			options: {
				browsers: ['last 3 versions','ie 9'],
				cascade: true,
				remove: true
			},
			all: {
				files: [{
					expand: true,
					cwd: '<%= config.tmp %>/styles',
					src: 'main.css',
					dest: '<%= config.dist %>/styles',
				}]
			}
		},

		copy: {

			scripts: {
				files: [{
					expand: true,
					flatten: false,
					cwd: '<%= config.app %>/scripts',
					src: ['**/*.js', '!config*.js'],
					dest: '<%= config.dist %>/scripts'
				}]
			},

			// Somente scripts minificados
			depsScriptsMin: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: 'bower_components',
						src: [
							'jquery/dist/jquery.min.js',
							'jquery/dist/jquery.min.map',
							'angular/angular.min.js',
							'angular/angular.min.js.map',
							'underscore/underscore-min.js',
							'underscore/underscore-min.map',
							'bootstrap/dist/js/bootstrap.min.js',
							'angular-bootstrap/ui-bootstrap.min.js',
							'angular-bootstrap/ui-bootstrap-tpls.min.js',
							'angular-route/angular-route.min.js',
							'angular-route/angular-route.min.js.map',
							'angular-animate/angular-animate.min.js',
							'angular-animate/angular-animate.min.js.map',
							'angular-cookies/angular-cookies.min.js',
							'angular-input-masks/angular-input-masks-standalone.min.js',
							'remarkable-bootstrap-notify/dist/bootstrap-notify.min.js',
							'bootstrap-material-design/dist/js/material.min.js',
							'bootstrap-material-design/dist/js/material.min.js.map',
							'bootstrap-material-design/dist/js/ripples.min.js',
							'bootstrap-material-design/dist/js/ripples.min.js.map',
							'dropzone/dist/min/dropzone.min.js',
							'angular-bootstrap-multiselect/dist/angular-bootstrap-multiselect.min.js'
						],
						dest: '<%= config.dist %>/scripts'
					},

					{
						expand: true,
						flatten: true,
						cwd: 'bower_components',
						src: [
							'ui-cropper/compile/minified/ui-cropper.js'
						],
						dest: '<%= config.dist %>/scripts',
						rename: function(dest, src) {
							return dest + "/" + src.replace(/\.js$/, ".min.js");
						}
					}
				]
			},

			// Somente scripts não minificados que possuir versão minificada
			depsScriptsSrc: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: 'bower_components',
						src: [
							'jquery/dist/jquery.js',
							'angular/angular.js',
							'bootstrap/dist/js/bootstrap.js',
							'angular-bootstrap/ui-bootstrap.js',
							'angular-bootstrap/ui-bootstrap-tpls.js',
							'angular-route/angular-route.js',
							'angular-animate/angular-animate.js',
							'angular-cookies/angular-cookies.js',
							'angular-input-masks/angular-input-masks-standalone.js',
							'remarkable-bootstrap-notify/dist/bootstrap-notify.js',
							'bootstrap-material-design/dist/js/material.js',
							'bootstrap-material-design/dist/js/ripples.js',
							'dropzone/dist/dropzone.js',
							'ui-cropper/compile/unminified/ui-cropper.js',
							'angular-bootstrap-multiselect/dist/angular-bootstrap-multiselect.js'
						],
						dest: '<%= config.dist %>/scripts',
						rename: function(dest, src) {
							return dest + "/" + src.replace(/\.js$/, ".min.js");
						}
					},

					{
						expand: true,
						flatten: true,
						cwd: 'bower_components',
						src: [
							'underscore/underscore.js'
						],
						dest: '<%= config.dist %>/scripts',
						rename: function(dest, src) {
							return dest + "/" + src.replace(/\.js$/, "-min.js");
						}
					}

				]

			},

			deps: {
				files: [

					// Scripts que não possuiem versão minificada
					{
						expand: true,
						flatten: true,
						cwd: 'bower_components',
						src: [
							'blockUI/jquery.blockUI.js',
							'angular-i18n/angular-locale_pt-br.js'
						],
						dest: '<%= config.dist %>/scripts',
					},

					{
						expand: true,
						flatten: true,
						cwd: 'bower_components/bootstrap/fonts',
						src: ['*'],
						dest: '<%= config.dist %>/fonts'
					},

					{
						expand: true,
						flatten: true,
						cwd: 'bower_components/material-design-icons/iconfont',
						src: ['*.{woff,woff2,ttf,eot}'],
						dest: '<%= config.dist %>/styles'
					},

					{
						expand: true,
						flatten: true,
						cwd: 'bower_components/mdi/fonts',
						src: ['*.{woff,woff2,ttf,eot}'],
						dest: '<%= config.dist %>/fonts'
					}

				]

			},

			images: {
				files: [
					{
						expand: true,
						flatten: false,
						cwd: '<%= config.app %>/images/',
						src: ['**/*.{png,jpg,jpeg,gif,webp,ico,svg}'],
						dest: '<%= config.dist %>/images'
					}
				]
			},

			fonts: {
				files: [
					{
						expand: true,
						flatten: false,
						cwd: '<%= config.app %>/fonts/',
						src: ['**/*'],
						dest: '<%= config.dist %>/fonts'
					}
				]
			},

			manual: {
				files: [
					{
						expand: true,
						flatten: false,
						cwd: '<%= config.app %>/manual/',
						src: ['**/*', '!**/*.adoc', '!**/*.pdfmarks'],
						dest: '<%= config.dist %>/manual'
					}
				]
			},

			config: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: '<%= config.app %>/scripts',
						src: ['config.js'],
						dest: '<%= config.dist %>/scripts',
						rename: function(dest) {
							return dest + '/appConfig.js';
						}
					}
				]
			},

			configBuild: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: '<%= config.app %>/scripts',
						src: ['config.js'],
						dest: '<%= config.app %>/scripts',
						rename: function(dest) {
							return dest + '/appConfig.js';
						}
					}
				]
			},


			configDeploy: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: '<%= config.app %>/scripts',
						src: ['config.deploy.js'],
						dest: '<%= config.app %>/scripts',
						rename: function(dest) {
							return dest + '/appConfig.js';
						}
					}
				]
			},

			configHomolog: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: '<%= config.app %>/scripts',
						src: ['config.homolog.js'],
						dest: '<%= config.app %>/scripts',
						rename: function(dest) {
							return dest + '/appConfig.js';
						}
					}
				]
			},

			configProducao: {
				files: [
					{
						expand: true,
						flatten: true,
						cwd: '<%= config.app %>/scripts',
						src: ['config.producao.js'],
						dest: '<%= config.app %>/scripts',
						rename: function(dest) {
							return dest + '/appConfig.js';
						}
					}
				]
			}

		},

		clean: {

			options: { force: true },

			all: {
				files: [{
					dot: true,
					src: [
						'<%= config.tmp %>/*',
						'<%= config.dist %>/*',
						'!<%= config.dist %>/.git*'
					]
				}]
			},

			build: {
				src: [
					'<%= config.app %>/scripts/appConfig.js',
					'<%= config.app %>/views/index.pug.tmp'
				]
			}

		},

		pugUsemin: {

			scripts: {
				options: {
					tasks: {
						js: ['ngmin', 'uglify'],
						css: ['concat', 'cssmin']
					},
					prefix: '<%= config.app %>',
					targetPrefix: '<%= config.dist %>'
				},
				files: [{
					src: '<%= config.app %>/views/index.pug',
					dest: '<%= config.app %>/views/index.pug.tmp'
				}]
			}

		},

		jshint: {
			options: {
				jshintrc: '.jshintrc'
			},
			all: [
				'Gruntfile.js',
				'<%= config.app %>/scripts/**/*.js'
			]
		},

		bower: {
			install: {
				options: {
					install: true,
					verbose: true,
					copy: false
				}
			}
		}

	});

	var PugInheritance = require('pug-inheritance');
	var changedPugs = [];

	var onPugChange = grunt.util._.debounce(function() {

		var options = grunt.config('pug.all.options');
		var dependantFiles = [];

		changedPugs.forEach(function(filename) {

			var directory = options.basedir;
			var inheritance = new PugInheritance(filename, directory, options);

			dependantFiles = dependantFiles.concat(inheritance.files);
		});

		var compileFiles = [];
		config.pugs.forEach(function(file){

			if(dependantFiles.indexOf(file) !== -1){
				compileFiles.push(file);
			}

		});

		var taskConfig = grunt.config('pug.all.files')[0];

		taskConfig.src = compileFiles;
		grunt.config('pug.all.files', [taskConfig]);

		changedPugs = [];

	}, 200);

	var changedScripts = [];

	var onScriptChange = grunt.util._.debounce(function() {

		grunt.config('jshint.all', changedScripts);

		var taskConfig = grunt.config('copy.scripts.files')[0];

		var copyList = [];

		for (var i = 0; i < changedScripts.length; i++) {
			copyList.push(changedScripts[i].replace(taskConfig.cwd + '/', ''));
		}

		taskConfig.src = copyList;

		grunt.config('copy.scripts.files', [taskConfig]);

		changedScripts = [];

	}, 200);

	grunt.event.on('watch', function(action, filepath) {

		if(filepath.indexOf(".pug") !== -1) {
			changedPugs.push(filepath);
			onPugChange();
		} else if(filepath.indexOf(".js") !== -1) {
			changedScripts.push(filepath);
			onScriptChange();
		}

	});

	grunt.registerTask('default', [
		'bower:install',
		'jshint',
		'clean:all',
		'less',
		'autoprefixer',
		'pug:all',
		'copy:scripts',
		'copy:images',
		'copy:fonts',
		'copy:depsScriptsSrc',
		'copy:deps',
		'copy:manual',
		'copy:config',
		'watch'
	]);

	grunt.registerTask('build', function (target) {

		grunt.task.run([
			'bower:install',
			'jshint',
			'clean:all',
			'copy:configBuild'
		]);

		if (target === 'deploy') {

			grunt.task.run(['copy:configDeploy']);

		} else if (target === 'homolog') {

			grunt.task.run(['copy:configHomolog']);

		} else if (target === 'producao') {

			grunt.task.run(['copy:configProducao']);

		}

		grunt.task.run([
			'pugUsemin',
			'less',
			'autoprefixer',
			'pug:all',
			'pug:dist',
			'copy:deps',
			'copy:depsScriptsMin',
			'copy:images',
			'copy:fonts',
			'copy:manual',
			'clean:build'
		]);

	});

};
