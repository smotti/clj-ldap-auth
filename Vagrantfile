# -*- mode: ruby -*-
# vi: set ft=ruby :
Vagrant.configure(2) do |config|
  config.vm.box = "debian/wheezy64"
  config.vm.box_version = "7.9.0"
  config.vm.box_check_update = false
  # OpenLDAP
  config.vm.network "forwarded_port", guest: 389, host: 3890
  # PHPLDAPAdmin
  config.vm.network "forwarded_port", guest: 80, host: 8888
  config.vm.provider "virtualbox" do |vb|
    vb.name = "clj-ldap-auth-dev"
    vb.cpus = "1"
    vb.memory = "256"
  end
  config.vm.provision "ansible_local" do |a|
    a.version = "latest"
    a.install = true
    a.playbook = "provision/playbook.yml"
  end
end
