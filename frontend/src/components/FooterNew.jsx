import { Heart, Mail, Phone, MapPin } from "lucide-react";

const Footer = () => {
  return (
    <footer className="bg-foreground text-background py-12">
      <div className="container mx-auto px-4">
        <div className="grid md:grid-cols-4 gap-8 mb-8">
          <div>
            <div className="flex items-center gap-2 mb-4">
              <Heart className="h-6 w-6 text-primary" fill="currentColor" />
              <span className="font-poppins text-xl font-bold">MediWay</span>
            </div>
            <p className="text-background/70 leading-relaxed">
              Your trusted digital healthcare partner across Sri Lanka.
            </p>
          </div>
          <div>
            <h3 className="font-poppins font-semibold mb-4">Quick Links</h3>
            <ul className="space-y-2">
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">About Us</a></li>
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Find Hospitals</a></li>
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Book Appointment</a></li>
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Health Blog</a></li>
            </ul>
          </div>
          <div>
            <h3 className="font-poppins font-semibold mb-4">Support</h3>
            <ul className="space-y-2">
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Help Center</a></li>
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Privacy Policy</a></li>
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Terms of Service</a></li>
              <li><a href="#" className="text-background/70 hover:text-primary transition-colors">Contact Us</a></li>
            </ul>
          </div>
          <div>
            <h3 className="font-poppins font-semibold mb-4">Get in Touch</h3>
            <ul className="space-y-3">
              <li className="flex items-center gap-2 text-background/70">
                <Phone className="h-4 w-4 text-primary" />
                <span>+94 11 234 5678</span>
              </li>
              <li className="flex items-center gap-2 text-background/70">
                <Mail className="h-4 w-4 text-primary" />
                <span>hello@mediway.lk</span>
              </li>
              <li className="flex items-start gap-2 text-background/70">
                <MapPin className="h-4 w-4 text-primary mt-1" />
                <span>123 Galle Road,<br />Colombo 03, Sri Lanka</span>
              </li>
            </ul>
          </div>
        </div>
        <div className="border-t border-background/20 pt-8 text-center text-background/70">
          <p>&copy; {new Date().getFullYear()} MediWay. All rights reserved. Made with <Heart className="inline h-4 w-4 text-primary" fill="currentColor" /> for Sri Lanka.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
