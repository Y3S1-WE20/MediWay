import { Card, CardContent } from "@/components/ui/card";
import { Calendar, CreditCard, Hospital, QrCode } from "lucide-react";

const features = [
  {
    icon: Calendar,
    title: "Easy Appointments",
    description: "Book, reschedule, or cancel appointments with your preferred healthcare providers instantly — anytime, anywhere."
  },
  {
    icon: QrCode,
    title: "Smart Health Card",
    description: "Your digital health card with QR code access. No more carrying physical documents to hospitals."
  },
  {
    icon: CreditCard,
    title: "Secure Payments",
    description: "Pay consultation fees and bills securely online. Support for all major payment methods in Sri Lanka."
  },
  {
    icon: Hospital,
    title: "Hospital Insights",
    description: "Browse hospitals, read reviews, compare services, and make informed healthcare decisions."
  }
];

const Features = () => {
  return (
    <section className="py-20 bg-background">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12 animate-fade-in">
          <h2 className="font-poppins text-3xl md:text-4xl font-bold mb-4">
            Everything You Need for Better Healthcare
          </h2>
          <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
            Comprehensive digital health services designed for modern Sri Lanka
          </p>
        </div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6 max-w-7xl mx-auto">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <Card 
                key={index} 
                className="group hover:shadow-card-hover transition-all duration-300 cursor-pointer border-none shadow-card animate-slide-up"
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                <CardContent className="p-6 text-center">
                  <div className="w-16 h-16 mx-auto mb-4 bg-gradient-primary rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300">
                    <Icon className="h-8 w-8 text-white" />
                  </div>
                  <h3 className="font-poppins text-xl font-semibold mb-2 text-foreground">
                    {feature.title}
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    {feature.description}
                  </p>
                </CardContent>
              </Card>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default Features;
