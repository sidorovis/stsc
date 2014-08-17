class base{
  group { "puppet":
    ensure => "present",
  }
  exec { 'apt-get update':
    command => '/usr/bin/apt-get update',
  }

  package { "mc" :
    ensure => present,
    require => Exec['apt-get update']
  }

  package { "git" :
    ensure => present,
    require => Exec['apt-get update']
  }

#  package { "openjdk-7-jdk" :
#    ensure => present,
#    require => Exec['apt-get update']
#  }

  package { "python-software-properties":
	ensure => present,
	require => Exec['apt-get update']
  }

  exec { 'create-repository-for-oracle-java':
    command => '/usr/bin/add-apt-repository ppa:webupd8team/java',
	require => Package['python-software-properties']
  }

  exec { 'webupd8team-repository apt-get update':
    command => '/usr/bin/apt-get update',
	require => Exec['create-repository-for-oracle-java']
  }

  exec { 'create-auto-accepted-oracle':
    command => '/bin/echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections',
	require => Exec['webupd8team-repository apt-get update']
  }

  package { "oracle-java7-installer":
	ensure => present,
	require => Exec['create-auto-accepted-oracle']
  }

  exec { 'oracle-java7-set-default':
    command => '/usr/bin/apt-get install oracle-java7-set-default',
	require => Package['oracle-java7-installer']
  }

  file { "/root/.ssh":
    ensure => "directory",
  }

  file { 
  "/root/.ssh/config":
    source => "puppet:///modules/base/ssh_config",
    mode => 600,
    owner => root,
    group => root,
  }

  file {
    "/root/.ssh/id_rsa":
      source => "puppet:///modules/base/id_rsa",
      mode => 600,
      owner => root,
      group => root,
  }

  file {
    "/root/.ssh/id_rsa.pub":
      source => "puppet:///modules/base/id_rsa.pub",
      mode => 644,
      owner => root,
      group => root,
  }

  ssh_authorized_key { "ssh_key":
    ensure => "present",
    key    => "AAAAB3NzaC1yc2EAAAADAQABAAABAQCeHdBPVGuSPVOO+n94j/Y5f8VKGIAzjaDe30hu9BPetA+CGFpszw4nDkhyRtW5J9zhGKuzmcCqITTuM6BGpHax9ZKP7lRRjG8Lh380sCGA/691EjSVmR8krLvGZIQxeyHKpDBLEmcpJBB5yoSyuFpK+4RhmJLf7ImZA7mtxhgdPGhe6crUYRbLukNgv61utB/hbre9tgNX2giEurBsj9CI5yhPPNgq6iP8ZBOyCXgUNf37bAe7AjQUMV5G6JMZ1clEeNPN+Uy5Yrfojrx3wHfG40NuxuMrFIQo5qCYa3q9/SVOxsJILWt+hZ2bbxdGcQOd9AXYFNNowPayY0BdAkSr",
    type   => "ssh-rsa",
    user   => "root",
    require => File['/root/.ssh/id_rsa.pub']
    }


  file { "/etc/motd":
    source => "puppet:///modules/base/motd",
    mode => 644,
  }

}
