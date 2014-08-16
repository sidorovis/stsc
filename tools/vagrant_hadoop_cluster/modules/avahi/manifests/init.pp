class avahi{
  package { "avahi-daemon":
    ensure => "installed",
    require => Exec['apt-get update']
  }

  file { "/etc/avahi/avahi-daemon.conf":
    source => "puppet:///modules/avahi/avahi-daemon.conf",
    owner => root,
    group => root,
    notify  => Service["avahi-daemon"],
    require => Package["avahi-daemon"]
  }

  service{ "avahi-daemon":
    ensure     => "running",
    enable => true,
    require =>  File['/etc/avahi/avahi-daemon.conf']
  }

  file{ "/etc/hosts":
    source => "puppet:///modules/avahi/hosts",
    owner => root,
    group => root,
  }

  file{ "/etc/avahi/hosts":
    source => "puppet:///modules/avahi/hosts",
    owner => root,
    group => root,
    notify => Service["avahi-daemon"]
  }
}
