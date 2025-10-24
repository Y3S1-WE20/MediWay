import aboutImage from "@/assets/about-healthcare.jpg";

const About = () => {
  return (
    <section className="py-20 bg-gradient-subtle">
      <div className="container mx-auto px-4">
        <div className="grid md:grid-cols-2 gap-12 items-center max-w-6xl mx-auto">
          <div className="animate-fade-in">
            <img 
              src={aboutImage} 
              alt="Digital healthcare technology with health card and smartphone"
              className="rounded-2xl shadow-card-hover w-full"
            />
          </div>
          <div className="animate-slide-up">
            <h2 className="font-poppins text-3xl md:text-4xl font-bold mb-6 text-foreground">
              Connecting Patients & Providers Across Sri Lanka
            </h2>
            <p className="text-muted-foreground text-lg mb-6 leading-relaxed">
              MediWay was founded with a simple mission: to make quality healthcare accessible and affordable for every Sri Lankan family.
            </p>
            <p className="text-muted-foreground text-lg mb-6 leading-relaxed">
              We bridge the gap between patients and healthcare providers through innovative digital solutions, ensuring seamless communication, secure data management, and convenient access to medical services.
            </p>
            <p className="text-muted-foreground text-lg leading-relaxed">
              Whether you're in Colombo or Jaffna, MediWay empowers you to take control of your health journey with confidence and ease.
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default About;
