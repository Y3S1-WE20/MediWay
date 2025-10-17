import { Card, CardContent } from "@/components/ui/card";
import { Quote } from "lucide-react";

const testimonials = [
  {
    name: "Priya Fernando",
    role: "Teacher, Colombo",
    quote: "MediWay made it so easy to book my daughter's vaccination appointment. The entire process was seamless and the reminders were very helpful!",
    avatar: "PF"
  },
  {
    name: "Rajesh Kumar",
    role: "Business Owner, Kandy",
    quote: "Finally, a platform that connects me with quality healthcare providers. I can now manage my family's health records in one secure place.",
    avatar: "RK"
  },
  {
    name: "Nimali Perera",
    role: "Software Engineer, Galle",
    quote: "The smart health card feature is amazing! I no longer need to carry physical documents. Everything is accessible on my phone.",
    avatar: "NP"
  }
];

const PatientStories = () => {
  return (
    <section className="py-20 bg-secondary/30">
      <div className="container mx-auto px-4">
        <div className="text-center mb-12 animate-fade-in">
          <h2 className="font-poppins text-3xl md:text-4xl font-bold mb-4">
            What Our Patients Say
          </h2>
          <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
            Join thousands of Sri Lankans who trust MediWay for their healthcare needs
          </p>
        </div>

        <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
          {testimonials.map((testimonial, index) => (
            <Card key={index} className="h-full shadow-card hover:shadow-card-hover transition-all duration-300 border-none">
              <CardContent className="p-6">
                <Quote className="h-8 w-8 text-primary mb-4 opacity-60" />
                <p className="text-foreground mb-6 leading-relaxed">
                  "{testimonial.quote}"
                </p>
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-full bg-gradient-primary flex items-center justify-center text-white font-semibold">
                    {testimonial.avatar}
                  </div>
                  <div>
                    <p className="font-semibold text-foreground">{testimonial.name}</p>
                    <p className="text-sm text-muted-foreground">{testimonial.role}</p>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
};

export default PatientStories;
