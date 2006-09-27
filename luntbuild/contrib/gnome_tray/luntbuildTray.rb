require 'hessianProxy'
require 'gtktrayicon'

Gtk.init

#Edit these to suit your luntbuild installation
host = "localhost"
port = 8080
project_name = "componentA"
schedule_name = "nightly"
username="luntbuild"
password="luntbuild"
#End of settings 

schedule_status_created = 0;
schedule_status_success = 1;
schedule_status_failed = 2;
schedule_status_running = 3;
old_status_code=-1

images = ["unknown.gif", "success.gif", "failed.gif", "running.gif"]

lunt_url = "http://#{host}:#{port}/luntbuild/app?service=hessian"
puts ("Requesting #{lunt_url}")
proxy = Hessian::HessianProxy.new(lunt_url, username, password)
tray = Gtk::TrayIcon.new("test", Gdk::Screen.default)

Thread.new do
  while (true)
    puts("Getting schedule")
    begin
      schedule = proxy.getScheduleByName(project_name, schedule_name)
    rescue Exception => e
      puts("Got exception: " + e)
      status_code=0
    else
      puts("Got schedule")
      status_code = schedule['status']
    end
    puts("Statuscode: #{status_code}")
    if(old_status_code != status_code)
      puts("about to change status")
      if(old_status_code != -1)
        puts("removing image")
        tray.each{|widget|
          tray.remove(widget)
        }
        puts("removed image")
      end
      status_image = images[status_code]
      image_path="images\/#{status_image}"
      puts("imagepath: #{image_path}")
      image = Gtk::Image.new(image_path)
      tray.add(image)
      old_status_code = status_code
    end
    tray.show_all
    puts("Sleeping ..." + Time.now.asctime)
    sleep 15
  end
end

Gtk.main
