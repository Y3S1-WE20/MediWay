import { motion } from "framer-motion";
import { Calendar, QrCode, CreditCard, BarChart3 } from "lucide-react";

const features = [
  {
    icon: Calendar,
    title: "Easy Appointments",
    description: "Book, reschedule, or cancel appointments instantly with just a few taps.",
  },
  {
    icon: QrCode,
    title: "Smart Health Card",
    description: "QR-based digital health card for quick access to your complete medical history.",
  },
  {
    icon: CreditCard,
    title: "Secure Payments",
    description: "Pay for consultations and services safely with multiple payment options.",
  },
  {
    icon: BarChart3,
    title: "Hospital Insights",
    description: "Track your health metrics and get personalized insights from your healthcare provider.",
  },
];

const FeaturesSection = () => {
  return (
    <section className="py-20 px-4 bg-muted/30">
      <div className="container mx-auto max-w-6xl">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
          className="text-center mb-16"
        >
          <h2 className="text-3xl md:text-4xl font-bold text-foreground mb-4">
            Everything You Need for Better Healthcare
          </h2>
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            MediWay brings together all essential healthcare services in one simple platform
          </p>
        </motion.div>

        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true }}
                transition={{ duration: 0.5, delay: index * 0.1 }}
                whileHover={{ y: -8, transition: { duration: 0.2 } }}
                className="group"
              >
                <div className="h-full bg-card rounded-2xl p-6 shadow-[0_2px_12px_rgba(0,0,0,0.08)] hover:shadow-[0_8px_30px_rgba(0,0,0,0.12)] transition-all duration-300">
                  <div className="w-14 h-14 rounded-xl bg-primary/10 flex items-center justify-center mb-4 group-hover:bg-primary/20 transition-colors">
                    <Icon className="w-7 h-7 text-primary" />
                  </div>
                  <h3 className="text-xl font-semibold text-foreground mb-2">
                    {feature.title}
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    {feature.description}
                  </p>
                </div>
              </motion.div>
            );
          })}
        </div>
      </div>
    </section>
  );
};

export default FeaturesSection;
